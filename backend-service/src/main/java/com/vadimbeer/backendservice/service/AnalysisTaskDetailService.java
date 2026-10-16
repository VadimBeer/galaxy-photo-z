package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.AnalysisTaskWithPhotoAndChartDto;
import com.vadimbeer.backendservice.DTO.QuantilePointDto;
import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.jpa.FileRepository;
import com.vadimbeer.backendservice.jpa.QuantileRepository;
import com.vadimbeer.backendservice.models.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisTaskDetailService {

    private final AnalysisTaskRepository taskRepository;
    private final AnalysisResultService resultService;
    private final QuantileRepository quantileRepository;
    private final FileRepository fileRepository;
    private final AnalysisTaskMapper mapper;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public AnalysisTaskWithPhotoAndChartDto findTasksAndPhotoAndChart(Long taskId, User user) {
        AnalysisTask analysisTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id : " + taskId + " не найдена."));

        if(!analysisTask.getFile().getUser().getId().equals(user.getId())) {
            log.info("Пользователь {} запросил недоступную для него задачу {}", user, analysisTask);
            throw new RuntimeException("У вас нет доступа к данному файлу.");
        }

        FilesStorage galaxyImg = analysisTask.getFile();
        FilesStorage galaxyChartImg = fileRepository
                .findByObjectKey("charts/" + galaxyImg.getObjectKey())
                .orElse(null);

        ModelVersion model = analysisTask.getModelVersion();

        AnalysisResult result = resultService.findByTaskId(taskId)
                .orElse(null);

        List<QuantilePointDto> quantiles = result == null
                ? List.of()
                : quantileRepository.findByTaskIdOrderByTauAsc(taskId).stream()
                .map(q -> new QuantilePointDto(
                        q.getAnalysisResultQuantileId().getTau(),
                        q.getValue()
                ))
                .toList();

        String galaxyImgUrl = storageService.getReadUrl(
                galaxyImg.getObjectKey()
        );

        String galaxyChartImgUrl = galaxyChartImg == null
                ? null
                : storageService.getReadUrl(galaxyChartImg.getObjectKey());

        return mapper.toDtoWithPhotoAndChart(
                analysisTask,
                galaxyImg,
                galaxyChartImg,
                galaxyImgUrl,
                galaxyChartImgUrl,
                model,
                result,
                quantiles
                );
    }
}
