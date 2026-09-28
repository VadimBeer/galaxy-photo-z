package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.UploadResponse;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@RestController
@RequestMapping("api/v1/galaxy-photos")
@Slf4j
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
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        if (imgFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(fileUploadService.saveImage(user, imgFile));
    }

}
