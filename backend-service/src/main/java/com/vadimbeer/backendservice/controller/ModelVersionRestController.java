package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.ModelVersionRequest;
import com.vadimbeer.backendservice.models.ModelVersion;
import com.vadimbeer.backendservice.service.ModelVersionService;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<ModelVersionRequest> getAllModels() {
        return modelVersionService.getAll();
    }
}
