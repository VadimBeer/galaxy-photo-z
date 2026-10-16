package com.vadimbeer.backendservice;

import com.vadimbeer.backendservice.props.UploadProperties;
import com.vadimbeer.backendservice.service.StorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.InsufficientDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    private static final String BUCKET = "galaxy-photos";
    private static final String OBJECT_KEY = "galaxies/ngc-1300.png";
    private static final byte[] DATA = "fake-image-bytes".getBytes();

    @Mock
    private MinioClient minioClient;

    @Mock
    private UploadProperties uploadProperties;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        Set<String> allowed = new HashSet<>(Set.of("image/jpeg", "image/png", "image/fits"));

        when(uploadProperties.getBucketName()).thenReturn(BUCKET);
        when(uploadProperties.getAllowedFileTypes()).thenReturn(allowed);

        storageService = new StorageService(minioClient, uploadProperties);
    }

    private InputStream stream() {
        return new ByteArrayInputStream(DATA);
    }

    // ---------- Успешная загрузка ----------

    @Test
    @DisplayName("Допустимый тип: файл уходит в MinIO с правильными параметрами")
    void saveImage_allowedType_callsPutObjectWithCorrectArgs() throws Exception {
        storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, "image/png");

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());

        PutObjectArgs args = captor.getValue();
        assertEquals(BUCKET, args.bucket());
        assertEquals(OBJECT_KEY, args.object());
        // в minio 9.x contentType() возвращает okhttp3.MediaType, а не String
        assertEquals("image/png", args.contentType().toString());
        assertEquals(DATA.length, args.objectSize());
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "image/png", "image/fits"})
    @DisplayName("Все разрешённые в конфиге типы загружаются")
    void saveImage_eachAllowedType_isUploaded(String contentType) throws Exception {
        storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, contentType);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    // ---------- Недопустимые типы ----------

    @ParameterizedTest
    @ValueSource(strings = {
            "application/pdf",
            "text/plain",
            "image/gif",
            "image/webp",  // не входит в конфиг
            "png",         // расширение вместо MIME-типа: регрессия на старый баг с конфигом
            "IMAGE/PNG",   // проверка регистрозависимая, фиксируем текущее поведение
            ""
    })
    @DisplayName("Недопустимый тип: MinIO не вызывается")
    void saveImage_notAllowedType_doesNotCallMinio(String contentType) {
        storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, contentType);

        verifyNoInteractions(minioClient);
    }

    @Test
    @DisplayName("contentType = null: MinIO не вызывается, исключения нет")
    void saveImage_nullContentType_doesNotCallMinio() {
        assertDoesNotThrow(() ->
                storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, null));

        verifyNoInteractions(minioClient);
    }

    // ---------- Обработка ошибок ----------

    @Test
    @DisplayName("MinioException из putObject глотается и не пробрасывается наружу")
    void saveImage_minioException_isSwallowed() throws Exception {
        doThrow(new InsufficientDataException("not enough data"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        assertDoesNotThrow(() ->
                storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, "image/png"));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("RuntimeException из putObject глотается")
    void saveImage_runtimeException_isSwallowed() throws Exception {
        doThrow(new IllegalStateException("boom"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        assertDoesNotThrow(() ->
                storageService.saveImageInStorage(stream(), OBJECT_KEY, DATA.length, "image/png"));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }
}