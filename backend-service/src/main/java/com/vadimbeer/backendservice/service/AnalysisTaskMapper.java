package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.AnalysisTaskDto;
import com.vadimbeer.backendservice.DTO.AnalysisTaskWithPhotoAndChartDto;
import com.vadimbeer.backendservice.DTO.QuantilePoint;
import com.vadimbeer.backendservice.DTO.QuantilePointDto;
import com.vadimbeer.backendservice.models.AnalysisResult;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.ModelVersion;
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

    public AnalysisTaskWithPhotoAndChartDto toDtoWithPhotoAndChart(
            AnalysisTask task,
            FilesStorage galaxyImg,
            FilesStorage galaxyChartImg,
            String galaxyImgUrl,
            String galaxyChartImgUrl,
            ModelVersion modelVersion,
            AnalysisResult result,
            List<QuantilePointDto> quantiles
    ) {
        return new AnalysisTaskWithPhotoAndChartDto(
                task.getId(),
                task.getCreatedAt(),

                galaxyImg.getId(),
                galaxyImg.getOriginalFileName(),
                galaxyImgUrl,

                galaxyChartImg == null ? null : galaxyChartImg.getId(),
                galaxyChartImgUrl,
                galaxyChartImg == null ? null : galaxyChartImg.getContentType(),

                modelVersion.getName(),
                modelVersion.getVersion(),

                result == null ? null : result.getMedian(),
                quantiles,
                galaxyImg.getContentType()
        );
    }
}
