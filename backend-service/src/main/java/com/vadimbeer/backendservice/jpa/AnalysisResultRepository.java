package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    @Query("""
        SELECT result.median
        FROM AnalysisResult result
        WHERE result.id = :id
    """)
    Double findMedianById(@Param("id") Long analysisTaskIds);
}
