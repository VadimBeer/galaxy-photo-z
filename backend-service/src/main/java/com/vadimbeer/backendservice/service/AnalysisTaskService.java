package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.AnalysisTaskDto;
import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.models.*;
import com.vadimbeer.backendservice.DTO.TaskCreatedEvent;
import jakarta.persistence.EntityNotFoundException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisTaskService {

    private final AnalysisTaskRepository repository;
    private final ModelVersionService modelVersionService;
    private final ApplicationEventPublisher event;
    private final AnalysisTaskMapper mapper;
    private final AnalysisResultService analysisResultService;

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

    @Transactional
    public AnalysisTask updateTask(ChartTaskConsume chartTaskConsume) {
        AnalysisTask analysisTask = repository.findById(
                chartTaskConsume.taskId()).orElseThrow(
                        EntityNotFoundException::new
        );

        analysisTask.setState(chartTaskConsume.state());

        if(chartTaskConsume.errorMessage() != null) {
            if (chartTaskConsume.errorMessage().equals("FAILED")) {
                analysisTask.setError(chartTaskConsume.errorMessage());
            }
        }

        return repository.save(analysisTask);
    }

    public AnalysisTask findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("AnalysisTask with id " + id + " not found")
                );
    }

    public List<AnalysisTaskDto> findRecentByUserId(User user) {
        List<AnalysisTask> tasksList = repository.findRecentByUserId(user.getId(), PageRequest.of(0, 7));
        List<Double> analysisResults = analysisResultService.findByAnalysisTaskIds(
                tasksList.stream()
                        .map(AnalysisTask::getId)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        List<AnalysisTaskDto> dtoList = mapper.toDtos(tasksList, analysisResults);
        return dtoList;
    }
}
