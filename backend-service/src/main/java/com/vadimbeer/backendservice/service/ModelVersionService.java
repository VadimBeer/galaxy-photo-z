package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ModelVersionMetricsResponse;
import com.vadimbeer.backendservice.DTO.ModelVersionResponse;
import com.vadimbeer.backendservice.jpa.ModelVersionRepository;
import com.vadimbeer.backendservice.models.ModelVersion;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class ModelVersionService {

    private final ModelVersionRepository modelVersionRepository;
    private final ModelVersionMapper modelVersionMapper;

    public List<ModelVersionResponse> getAll() {
        List<ModelVersionResponse> models = modelVersionRepository.findAll().stream()
                .map(modelVersionMapper::toModelVersionRequest)
                .toList();

        return models;
    }

    public ModelVersionMetricsResponse getById(int id) {
        ModelVersion modelVersion = modelVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ModelVersion with id " + id + " не найдено")
                );

        return modelVersionMapper.toModelVersionMetricsRequest(modelVersion);
    }

    public ModelVersion getModelVersionById(int id) {
        ModelVersion modelVersion = modelVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ModelVersion с id " + id + " не найдено"
                ));

        return modelVersion;
    }
}
