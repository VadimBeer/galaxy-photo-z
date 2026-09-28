package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.models.AnalysisTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisTaskRepository extends JpaRepository<AnalysisTask, Long> {
}
