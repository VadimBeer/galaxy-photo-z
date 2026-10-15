package com.vadimbeer.backendservice.DTO;

import com.vadimbeer.backendservice.models.AnalysisTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record AnalysisTaskWithPhotoAndChartDto(
        Long taskId,
        LocalDateTime createdAt,

        Long photoId,
        String photoFilename,
        String photoUrl,

        String modelName,
        String modelVersion,

        Long chartId,
        String chartUrl,
        String chartContentType,
        Double median,
        List<QuantilePointDto> quantiles
) {
}
