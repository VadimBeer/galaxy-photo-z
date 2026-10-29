package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.DownloadChartDto;
import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.props.UploadProperties;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.errors.MinioException;
import jakarta.persistence.EntityNotFoundException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@Service
public class FileStorageService {

    private final FileService fileService;
    private final StorageService storageService;

    public FileStorageService(FileService fileService, StorageService storageService,
                              UploadProperties uploadProperties
    ) {
        this.fileService = fileService;
        this.storageService = storageService;
    }

    public UploadResponse saveImage(User user, MultipartFile imgFile) {
        String objectKey = generateObjectKey();
        FilesStorage file;

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

    public DownloadChartDto getChartFileFromStorage(Long chartId, User user) {
        FilesStorage file = fileService.findById(chartId);

        if(file == null){
            log.warn("Файл с указанным идентификатором не найден: {}", chartId);
            throw new EntityNotFoundException("Файл с указанным идентификатором не найден: " + chartId);
        }

        if(user == null) {
            log.warn("User is null");
            throw new RuntimeException("Пользователь — null");
        }

        if(!user.equals(file.getUser())) {
            log.info("Пользователь не совпадает с владельцем файла");
            throw new RuntimeException("Пользователь не совпадает с владельцем файла");
        }

        DownloadChartDto dto;

        try (GetObjectResponse object = storageService.getObject(file.getObjectKey())){
             dto = new DownloadChartDto(
                    file.getOriginalFileName(),
                    object.readAllBytes()
            );
        }catch (IOException e){
            log.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return dto;
    }
}
