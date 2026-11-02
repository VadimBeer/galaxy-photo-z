package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.DownloadChartDto;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.FileStorageService;
import com.vadimbeer.backendservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/v1/experiments/download")
@RequiredArgsConstructor
public class DownloadRestController {

    private final FileStorageService fileStorageService;

    @GetMapping("/chart/{chartId}")
    public ResponseEntity<byte[]> getDownload(
            @PathVariable Long chartId,
            @AuthenticationPrincipal User user
    ) {

        DownloadChartDto dto =
                fileStorageService.getChartFileFromStorage(chartId, user);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.imageFilename() + "\""
                )
                .contentType(MediaType.IMAGE_PNG)
                .body(dto.file());
    }
}
