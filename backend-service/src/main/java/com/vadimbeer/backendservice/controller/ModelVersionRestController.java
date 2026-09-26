package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.ModelVersionMetricsResponse;
import com.vadimbeer.backendservice.DTO.ModelVersionResponse;
import com.vadimbeer.backendservice.service.ModelVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/model-version")
public class ModelVersionRestController {

    private ModelVersionService modelVersionService;

    public ModelVersionRestController(ModelVersionService modelVersionService) {
        this.modelVersionService = modelVersionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ModelVersionResponse>> getAllModels() {
        return ResponseEntity.ok(modelVersionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelVersionMetricsResponse> getModelVersionById(@PathVariable Integer id) {
        if(id == null) {
            return ResponseEntity.noContent().build();
        }

        ModelVersionMetricsResponse request = modelVersionService.getById(id);

        return ResponseEntity.ok(request);
    }
}
