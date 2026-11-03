package com.vadimbeer.backendservice.DTO;

public record ModelVersionMetricsResponse(
        Integer id,
        String name,
        String version,
        Double mae,
        Double mse,
        Double rmse,
        Double r2
) {
}
