package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.AnalysisTaskDto;
import com.vadimbeer.backendservice.DTO.AnalysisTaskWithPhotoAndChartDto;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.AnalysisTaskDetailService;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiments")
@RequiredArgsConstructor
public class AnalysisTaskRestController {

    private final AnalysisTaskService analysisTaskService;
    private final AnalysisTaskDetailService analysisTaskDetailService;

    @GetMapping("/lastExperiments")
    public ResponseEntity<List<AnalysisTaskDto>> getLastTask(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(analysisTaskService.findRecentByUserId(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisTaskWithPhotoAndChartDto> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        AnalysisTaskWithPhotoAndChartDto dto = analysisTaskDetailService
                .findTasksAndPhotoAndChart(id, user);

        return ResponseEntity.ok(dto);
    }
}
