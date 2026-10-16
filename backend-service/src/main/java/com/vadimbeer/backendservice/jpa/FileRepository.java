package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.DTO.ChartDtoOnDashboard;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FilesStorage, Long> {
    Optional<FilesStorage> findByObjectKey(String objectKey);
//    @Query("""
//               SELECT files.id, files.objectKey,
//                          files.originalFileName, task.modelVersion.name modelName,
//                          task.modelVersion.id modelVersion
//               FROM FilesStorage files
//               JOIN AnalysisTask task ON files.id = task.file.id
//               JOIN ModelVersion models ON models.id = task.modelVersion.id
//               WHERE files.user.id = :userId
//               ORDER BY files.createdAt DESC
//               LIMIT 1
//           """)
    Optional<FilesStorage> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
