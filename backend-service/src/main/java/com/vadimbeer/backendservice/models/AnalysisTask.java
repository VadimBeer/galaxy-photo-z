package com.vadimbeer.backendservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_task")
@Getter
public class AnalysisTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private State state;

    @Column(name = "error_message")
    private String error;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_version_id", nullable = false)
    private ModelVersion modelVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id")
    private FilesStorage file;

    public enum State {
        PENDING, PROCESSING, COMPLETED, FAILED
    }


    // Фабричный метод
    public static AnalysisTask fabricBuildAnalysisTask(ModelVersion modelVersion, FilesStorage file) {
        AnalysisTask task = new AnalysisTask();
        task.modelVersion = modelVersion;
        task.file = file;
        task.state = State.PENDING;
        return task;
    }
}
