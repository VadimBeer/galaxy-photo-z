package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private final FileService fileService;
    private final StorageService storageService;

    public FileUploadService(FileService fileService, StorageService storageService) {
        this.fileService = fileService;
        this.storageService = storageService;
    }

    public Integer saveImage(User user, MultipartFile file) {

    }
}
