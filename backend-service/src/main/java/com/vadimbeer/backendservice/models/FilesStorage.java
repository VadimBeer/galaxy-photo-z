package com.vadimbeer.backendservice.models;

import com.vadimbeer.backendservice.DTO.ChartTaskConsume;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files_storage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilesStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "object_key")
    private String objectKey;

    @Column(name = "original_filename")
    private String originalFileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreDestroy
    public void preDestroy() {
        deletedAt = LocalDateTime.now();
    }

    public static FilesStorage fabricChartFileStorage(String objectKey, User user, String filename, Long size) {
        FilesStorage filesStorage = new FilesStorage();
        filesStorage.objectKey = objectKey;
        filesStorage.user = user;
        filesStorage.originalFileName = filename;
        filesStorage.sizeBytes = size;

        return filesStorage;
    }
}
