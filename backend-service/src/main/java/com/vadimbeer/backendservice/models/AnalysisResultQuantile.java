package com.vadimbeer.backendservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "analysis_result_quantile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResultQuantile {

    @EmbeddedId
    private AnalysisResultQuantileId analysisResultQuantileId;

    @MapsId("taskId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private AnalysisResult analysisResult;

}
