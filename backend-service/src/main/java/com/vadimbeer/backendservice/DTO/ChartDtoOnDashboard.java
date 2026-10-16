package com.vadimbeer.backendservice.DTO;

public record ChartDtoOnDashboard(
        Long chartId,
        String urlObjectKey,
        String originalFileName,
        String modelName,
        String modelVersion
) {
}
