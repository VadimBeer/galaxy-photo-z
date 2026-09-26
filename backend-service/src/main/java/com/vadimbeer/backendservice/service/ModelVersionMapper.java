package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ModelVersionRequest;
import com.vadimbeer.backendservice.models.ModelVersion;
import org.springframework.stereotype.Service;

@Service
public class ModelVersionMapper {

    public ModelVersionRequest toModelVersionRequest(ModelVersion modelVersion) {
        return new ModelVersionRequest(
                modelVersion.getId(),
                modelVersion.getName(),
                modelVersion.getVersion()
        );
    }
}
