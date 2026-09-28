package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.jpa.FileRepository;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FilesStorage saveFileMetadata(String objectKey,String originalFileName,
                                 String contentType, Long sizeBytes,
                                 User user
    ) {
        FilesStorage filesStorage = new FilesStorage();

        filesStorage.setObjectKey(objectKey);
        filesStorage.setOriginalFileName(originalFileName);
        filesStorage.setContentType(contentType);
        filesStorage.setSizeBytes(sizeBytes);
        filesStorage.setUser(user);

        log.info("Метаданные сохранены для объекта по пути {} и с оригинальным названием {}",
                objectKey, originalFileName);

        return fileRepository.save(filesStorage);
    }

    public FilesStorage findById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Сущность с таким id не найдена")
                );
    }
}
