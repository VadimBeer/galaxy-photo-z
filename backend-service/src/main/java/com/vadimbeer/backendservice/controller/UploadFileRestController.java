package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.AnalysisTaskResponse;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.AnalysisService;
import com.vadimbeer.backendservice.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("api/v1/galaxy-photos")
@Slf4j
public class UploadFileRestController {

    private FileStorageService fileStorageService;
    private AnalysisService analysisService;

    @Autowired
    public UploadFileRestController(FileStorageService fileStorageService, AnalysisService analysisService) {
        this.fileStorageService = fileStorageService;
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisTaskResponse> uploudFile(
            @RequestParam MultipartFile imgFile,
            @RequestParam Integer modelId,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }


        AnalysisTaskResponse response = analysisService.submit(user, modelId, imgFile);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleInputError(
            ResponseStatusException exception
    ) {
        String message = exception.getReason() == null
                ? "Ошибка загрузки файла"
                : exception.getReason();

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of("message", message));
    }

}
