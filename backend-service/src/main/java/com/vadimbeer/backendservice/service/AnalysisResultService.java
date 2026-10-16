package com.vadimbeer.backendservice.service;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import com.vadimbeer.backendservice.jpa.AnalysisResultRepository;
import com.vadimbeer.backendservice.models.AnalysisResult;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.jpa.QuantileRepository;
import com.vadimbeer.backendservice.models.AnalysisResultQuantile;
import com.vadimbeer.backendservice.models.AnalysisResultQuantileId;
import org.springframework.transaction.annotation.Transactional;
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
    private final QuantileRepository quantileRepository;

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

    @Transactional
    public AnalysisResult save(AnalysisTask task, ChartTaskConsume message) {
        if (message.quantiles() == null || message.quantiles().isEmpty()) {
            throw new IllegalArgumentException(
                    "Для завершённой задачи " + task.getId()
                            + " не переданы квантили"
            );
        }

        AnalysisResult result = repository.save(new AnalysisResult(task, message.medianQuantile()));

        List<AnalysisResultQuantile> quantiles =
                message.quantiles()
                .stream()
                .map(point -> {
                    AnalysisResultQuantile quantile =
                            new AnalysisResultQuantile();

                    quantile.setAnalysisResultQuantileId(
                            new AnalysisResultQuantileId(
                                    result.getId(),
                                    point.tau()
                            )
                    );

                    quantile.setValue(point.value());
                    quantile.setAnalysisResult(result);

                    return quantile;
                })
                .toList();

        quantileRepository.saveAll(quantiles);

        return result;
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
