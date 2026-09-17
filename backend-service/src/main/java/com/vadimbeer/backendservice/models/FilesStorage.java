package com.vadimbeer.backendservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files_storage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilesStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectKey;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private LocalDateTime uploadAt;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
}
