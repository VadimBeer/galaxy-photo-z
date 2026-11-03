package com.vadimbeer.backendservice.DTO;

import com.vadimbeer.backendservice.models.AnalysisTask;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record AnalysisTaskWithPhotoAndChartDto(
        Long taskId,
        LocalDateTime createdAt,

        Long galaxyImgId,
        String galaxyImgFilename,
        String galaxyImgUrl,

        Long chartId,
        String chartUrl,
        String chartContentType,

        String modelName,
        String modelVersion,

        Double median,
        List<QuantilePointDto> quantiles,
        String inputContentType
) {
}
