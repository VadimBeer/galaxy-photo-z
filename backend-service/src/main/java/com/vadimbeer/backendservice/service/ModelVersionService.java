package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ModelVersionRequest;
import com.vadimbeer.backendservice.jpa.ModelVersionRepository;
import com.vadimbeer.backendservice.models.ModelVersion;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class ModelVersionService {

    private final ModelVersionRepository modelVersionRepository;
    private final ModelVersionMapper modelVersionMapper;

    public List<ModelVersionRequest> getAll() {
        List<ModelVersionRequest> models = modelVersionRepository.findAll().stream()
                .map(modelVersionMapper::toModelVersionRequest)
                .toList();

        return models;
    }
}
