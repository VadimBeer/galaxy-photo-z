package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.ModelVersion;
import com.vadimbeer.backendservice.DTO.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisTaskService {

    private final AnalysisTaskRepository repository;
    private final ModelVersionService modelVersionService;
    private final ApplicationEventPublisher event;

    public AnalysisTask save(AnalysisTask analysisTask) {
        return repository.save(analysisTask);
    }

    @Transactional
    public AnalysisTask create(Integer modelId, FilesStorage filesStorage) {
        ModelVersion modelVersion = modelVersionService.getModelVersionById(modelId);
        log.info("Версия модели {}", modelVersion.getName() + "_" + modelVersion.getVersion());

        AnalysisTask analysisTask = save(
                AnalysisTask.fabricBuildAnalysisTask(modelVersion, filesStorage));

        event.publishEvent(new TaskCreatedEvent(
                analysisTask.getId(),
                filesStorage.getUser().getId(),
                modelVersion.getId(),
                filesStorage.getObjectKey()
        ));

        return analysisTask;
    }
}
