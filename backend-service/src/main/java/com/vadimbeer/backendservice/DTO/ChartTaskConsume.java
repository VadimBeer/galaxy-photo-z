package com.vadimbeer.backendservice.DTO;

import com.vadimbeer.backendservice.models.AnalysisTask;

import java.util.List;

// DTO для приема объекта со стороны Python
public record ChartTaskConsume(
        Long taskId,
        Long userId,
        String chartObjectKey,
        List<QuantilePoint> quantiles,
        Double medianQuantile,
        AnalysisTask.State state,
        String errorMessage,
        String contentType
) {
}
