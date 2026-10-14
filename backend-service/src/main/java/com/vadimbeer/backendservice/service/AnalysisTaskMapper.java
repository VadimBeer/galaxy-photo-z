package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.AnalysisTaskDto;
import com.vadimbeer.backendservice.models.AnalysisTask;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisTaskMapper {

    public AnalysisTaskDto toDto(AnalysisTask task, Double median) {
        return new AnalysisTaskDto(
                task.getId(),
                task.getFile().getOriginalFileName(),
                task.getCreatedAt(),
                task.getModelVersion().getName(),
                task.getModelVersion().getVersion(),
                task.getState(),
                median
        );
    }

    public List<AnalysisTaskDto> toDtos(List<AnalysisTask> tasks, List<Double> medians) {
        List<AnalysisTaskDto> dtos = new ArrayList<>();
        for (int i  = 0; i < tasks.size(); i++) {
            dtos.add(toDto(tasks.get(i), medians.get(i)) );
        }

        return dtos;
    }
}
