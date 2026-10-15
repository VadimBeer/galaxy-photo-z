package com.vadimbeer.backendservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnalysisResultQuantileId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;

    private Double tau;

    public AnalysisResultQuantileId() {}

    public AnalysisResultQuantileId(Long taskId, Double tau) {
        this.taskId = taskId;
        this.tau = tau;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Double getTau() {
        return tau;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisResultQuantileId that)) return false;
        return Objects.equals(taskId, that.taskId) && Objects.equals(tau, that.tau);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, tau);
    }


}
