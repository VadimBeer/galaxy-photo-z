package com.vadimbeer.backendservice;

import com.vadimbeer.backendservice.jpa.FileRepository;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    private static final String OBJECT_KEY = "users/42/galaxies/ngc-1300.png";
    private static final String ORIGINAL_FILE_NAME = "моя галактика.png";
    private static final String CONTENT_TYPE = "image/png";
    private static final Long SIZE_BYTES = 123_456L;

    @Mock
    private FileRepository fileRepository;

    private FileService fileService;
    private User user;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository);
        user = new User();
        // Если у User есть setId/setUsername — можно проставить здесь, чтобы
        // тесты ниже были нагляднее, например user.setId(1L).
    }

    @Test
    @DisplayName("Метаданные корректно собираются в FilesStorage и передаются в repository.save")
    void saveFileMetadata_buildsEntityWithGivenFieldsAndSavesIt() {
        FilesStorage saved = new FilesStorage();
        when(fileRepository.save(any(FilesStorage.class))).thenReturn(saved);

        FilesStorage result = fileService.saveFileMetadata(
                OBJECT_KEY, ORIGINAL_FILE_NAME, CONTENT_TYPE, SIZE_BYTES, user);

        ArgumentCaptor<FilesStorage> captor = ArgumentCaptor.forClass(FilesStorage.class);
        verify(fileRepository).save(captor.capture());

        FilesStorage passedToRepo = captor.getValue();
        assertEquals(OBJECT_KEY, passedToRepo.getObjectKey());
        assertEquals(ORIGINAL_FILE_NAME, passedToRepo.getOriginalFileName());
        assertEquals(CONTENT_TYPE, passedToRepo.getContentType());
        assertEquals(SIZE_BYTES, passedToRepo.getSizeBytes());
        assertSame(user, passedToRepo.getUser());
    }

    @Test
    @DisplayName("Метод возвращает именно то, что вернул repository.save (а не собранный объект)")
    void saveFileMetadata_returnsWhatRepositoryReturns() {
        FilesStorage persisted = new FilesStorage();
        persisted.setObjectKey(OBJECT_KEY);
        when(fileRepository.save(any(FilesStorage.class))).thenReturn(persisted);

        FilesStorage result = fileService.saveFileMetadata(
                OBJECT_KEY, ORIGINAL_FILE_NAME, CONTENT_TYPE, SIZE_BYTES, user);

        assertSame(persisted, result);
    }

    @Test
    @DisplayName("sizeBytes = null передаётся в repository как есть, без подмены на 0")
    void saveFileMetadata_nullSizeBytes_isPassedThrough() {
        when(fileRepository.save(any(FilesStorage.class))).thenReturn(new FilesStorage());

        fileService.saveFileMetadata(OBJECT_KEY, ORIGINAL_FILE_NAME, CONTENT_TYPE, null, user);

        ArgumentCaptor<FilesStorage> captor = ArgumentCaptor.forClass(FilesStorage.class);
        verify(fileRepository).save(captor.capture());
        assertEquals(null, captor.getValue().getSizeBytes());
    }

    @Test
    @DisplayName("user = null передаётся в repository как есть")
    void saveFileMetadata_nullUser_isPassedThrough() {
        when(fileRepository.save(any(FilesStorage.class))).thenReturn(new FilesStorage());

        fileService.saveFileMetadata(OBJECT_KEY, ORIGINAL_FILE_NAME, CONTENT_TYPE, SIZE_BYTES, null);

        ArgumentCaptor<FilesStorage> captor = ArgumentCaptor.forClass(FilesStorage.class);
        verify(fileRepository).save(captor.capture());
        assertEquals(null, captor.getValue().getUser());
    }
}