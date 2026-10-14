package com.vadimbeer.backendservice.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_result")
public class AnalysisResult {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "task_id")
    AnalysisTask analysisTask;

    private Double median;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AnalysisResult() {

    }

    public AnalysisResult(AnalysisTask task, Double median) {
        this.analysisTask = task;
        this.median = median;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
