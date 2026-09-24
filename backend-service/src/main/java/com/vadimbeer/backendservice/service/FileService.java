package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.jpa.FileRepository;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class FileService {

    private FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FilesStorage saveFileMetadata(String originalFileName,
                                 String contentType, Long sizeBytes,
                                 User user
    ) {
        FilesStorage filesStorage = new FilesStorage();
        String objectKey = generateObjectKey();

        filesStorage.setObjectKey(objectKey);
        filesStorage.setOriginalFileName(originalFileName);
        filesStorage.setContentType(contentType);
        filesStorage.setSizeBytes(sizeBytes);
        filesStorage.setUser(user);

        return fileRepository.save(filesStorage);
    }

    //метод для генерации рандомного названия файла
    private String generateObjectKey() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }
}
