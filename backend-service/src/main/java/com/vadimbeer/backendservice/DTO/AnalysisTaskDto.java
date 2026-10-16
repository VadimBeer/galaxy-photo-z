package com.vadimbeer.backendservice.DTO;

import com.vadimbeer.backendservice.models.AnalysisTask;

import java.time.LocalDateTime;

public record AnalysisTaskDto(
        Long id,
        String filename,
        LocalDateTime dateTime,
        String modelName,
        String modelVersion,
        AnalysisTask.State state,
        Double median
) {
}
