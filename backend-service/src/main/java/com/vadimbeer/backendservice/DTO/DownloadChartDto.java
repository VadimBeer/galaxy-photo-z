package com.vadimbeer.backendservice.DTO;

public record DownloadChartDto(
        String imageFilename,
        byte[] file
) {
}
