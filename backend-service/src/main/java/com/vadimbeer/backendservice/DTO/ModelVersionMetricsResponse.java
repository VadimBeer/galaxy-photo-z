package com.vadimbeer.backendservice.DTO;

public record ModelVersionMetricsResponse(
        Integer id,
        String name,
        String version,
        double mae,
        double mse,
        double rmse,
        double r2
) {
}
