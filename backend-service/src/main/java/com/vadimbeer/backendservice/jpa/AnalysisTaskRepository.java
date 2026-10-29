package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.AnalysisTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface AnalysisTaskRepository extends JpaRepository<AnalysisTask, Long> {

    @Query("""
           SELECT task
           FROM AnalysisTask task
           WHERE task.file.user.id = :user_id
           ORDER BY task.createdAt DESC
           """)
    List<AnalysisTask> findRecentByUserId(@Param("user_id") Long user_id, Pageable pageable);

    @Query("""
        SELECT task 
        FROM AnalysisTask task
        WHERE task.file.id = :fileId
        """)
    AnalysisTask findByFilesStorageId(@Param("fileId") Long filesStorageId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT task FROM AnalysisTask task WHERE task.id = :id")
    Optional<AnalysisTask> findByIdForUpdate(@Param("id") Long id);
}
