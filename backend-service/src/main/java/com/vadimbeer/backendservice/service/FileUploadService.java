package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.config.UploadProperties;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@Service
public class FileUploadService {

    private final FileService fileService;
    private final StorageService storageService;
    private final UploadProperties uploadProperties;

    public FileUploadService(FileService fileService, StorageService storageService,
                             UploadProperties uploadProperties
    ) {
        this.fileService = fileService;
        this.storageService = storageService;
        this.uploadProperties = uploadProperties;
    }

    public UploadResponse saveImage(User user, MultipartFile imgFile) {
        String objectKey = generateObjectKey();
        FilesStorage file = null;

        try {
             file = fileService.saveFileMetadata(
                    objectKey,
                    imgFile.getOriginalFilename(),
                    imgFile.getContentType(),
                    imgFile.getSize(),
                    user
            );
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        try {
            storageService.saveImageInStorage(
                    imgFile.getInputStream(),
                    objectKey,
                    imgFile.getSize(),
                    imgFile.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new UploadResponse(file.getId(), file.getOriginalFileName());
    }

    //метод для генерации рандомного названия файла
    private String generateObjectKey() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }
}
