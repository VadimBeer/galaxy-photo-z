package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.models.ModelVersion;
import com.vadimbeer.backendservice.props.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ModelInputValidator {

    private final UploadProperties uploadProperties;

    private static final Set<String> CSV_TYPES = Set.of(
            "text/csv",
            "application/csv",
            "text/plain",
            "application/vnd.ms-excel"
    );

    public void validate(ModelVersion model, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw badRequest("Выберите непустой файл");
        }

        Long maxSize = uploadProperties.getMaxFileSize();
        if (maxSize != null && file.getSize() > maxSize) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Файл превышает допустимый размер"
            );
        }

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        filename = filename == null
                ? ""
                : filename.toLowerCase(Locale.ROOT);

        contentType = contentType == null
                ? ""
                : contentType.toLowerCase(Locale.ROOT);

        if ("CatBoost".equals(model.getName())
                && "V1".equals(model.getVersion())) {

            if (!filename.endsWith(".csv")
                    || !CSV_TYPES.contains(contentType)) {
                throw badRequest(
                        "Для CatBoost V1 нужен CSV с признаками одного объекта"
                );
            }

        } else if ("CNN".equals(model.getName())
                && "V2".equals(model.getVersion())) {

            boolean jpeg = (
                    filename.endsWith(".jpg")
                            || filename.endsWith(".jpeg")
            ) && "image/jpeg".equals(contentType);

            boolean png = filename.endsWith(".png")
                    && "image/png".equals(contentType);

            if (!jpeg && !png) {
                throw badRequest(
                        "Для CNN V2 нужно изображение JPEG или PNG"
                );
            }

        } else {
            throw badRequest(
                    "Загрузка данных для выбранной версии модели "
                            + "пока не поддерживается"
            );
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}