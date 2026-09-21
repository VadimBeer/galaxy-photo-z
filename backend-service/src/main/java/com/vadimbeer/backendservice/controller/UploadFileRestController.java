package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/galaxy-photos")
public class UploadFileRestController {

    private FileUploadService fileUploadService;

    public UploadFileRestController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploudFile(
            @RequestParam MultipartFile imgFile,
            @AuthenticationPrincipal User user
    ) {
        if(imgFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = imgFile.getOriginalFilename();
        return ResponseEntity.ok(new UploadResponse(fileName));
    }

}
