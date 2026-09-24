package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.config.UploadProperties;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        try {
            FilesStorage file = fileService.saveFileMetadata();
        }catch (Exception e){

        }

        return null;
    }
}
