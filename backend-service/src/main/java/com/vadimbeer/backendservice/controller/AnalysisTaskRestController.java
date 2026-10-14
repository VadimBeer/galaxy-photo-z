package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.AnalysisTaskDto;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiments")
@RequiredArgsConstructor
public class AnalysisTaskRestController {

    private final AnalysisTaskService analysisTaskService;

    @GetMapping("/all")
    public ResponseEntity<List<AnalysisTaskDto>> getLastTask(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(analysisTaskService.findRecentByUserId(user));
    }
}
