package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.AnalysisResultQuantile;
import com.vadimbeer.backendservice.models.AnalysisResultQuantileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuantileRepository extends JpaRepository<AnalysisResultQuantile, AnalysisResultQuantileId> {
    @Query("""
        SELECT q
        FROM AnalysisResultQuantile q
        WHERE q.analysisResultQuantileId.taskId = :taskId
        ORDER BY q.analysisResultQuantileId.tau ASC
        """)
    List<AnalysisResultQuantile> findByTaskIdOrderByTauAsc(
            @Param("taskId") Long taskId
    );
}
