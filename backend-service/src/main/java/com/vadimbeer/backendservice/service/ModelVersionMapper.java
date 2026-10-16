package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ModelVersionMetricsResponse;
import com.vadimbeer.backendservice.DTO.ModelVersionResponse;
import com.vadimbeer.backendservice.models.ModelVersion;
import org.springframework.stereotype.Service;

@Service
public class ModelVersionMapper {

    public ModelVersionResponse toModelVersionRequest(ModelVersion modelVersion) {
        return new ModelVersionResponse(
                modelVersion.getId(),
                modelVersion.getName(),
                modelVersion.getVersion()
        );
    }

    public ModelVersionMetricsResponse toModelVersionMetricsRequest(ModelVersion modelVersion) {
        return new ModelVersionMetricsResponse(
                modelVersion.getId(),
                modelVersion.getName(),
                modelVersion.getVersion(),
                modelVersion.getMae(),
                modelVersion.getMse(),
                modelVersion.getRmse(),
                modelVersion.getR2()
        );
    }
}
