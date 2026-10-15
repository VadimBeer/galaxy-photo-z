package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.jpa.AnalysisResultRepository;
import com.vadimbeer.backendservice.models.AnalysisResult;
import com.vadimbeer.backendservice.models.AnalysisTask;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisResultService {
    private final AnalysisResultRepository repository;

    public AnalysisResult findByAnalysisTaskId(Long analysisTaskId) {
        return repository.findById(analysisTaskId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Результат эксперимента с " + analysisTaskId + " не найден"
                )
        );
    }

    public List<Double> findByAnalysisTaskIds(List<Long> analysisTaskIds) {
        List<Double> results = new ArrayList<>();
        for(Long analysisTaskId : analysisTaskIds) {
            Double result = repository.findMedianById(analysisTaskId);
            results.add(result);
        }

        return results;
    }

    public AnalysisResult save(AnalysisTask task, ChartTaskConsume message) {
        AnalysisResult analysisResult = new AnalysisResult(task, message.medianQuantile());
        return repository.save(analysisResult);
    }

    public AnalysisResult findById(Long analysisTaskId) {
        return repository.findById(analysisTaskId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Результат задачи с таким id " + analysisTaskId + " не найден.")
                );
    }

    public Optional<AnalysisResult> findByTaskId(Long taskId) {
        return repository.findById(taskId);
    }
}
