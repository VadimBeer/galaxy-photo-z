package com.vadimbeer.backendservice.controller;

import com.vadimbeer.backendservice.DTO.ChartDtoOnDashboard;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.ChartFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/dashboard/chart")
@RequiredArgsConstructor
public class ChartDashboardRestController {

    private final ChartFileService chartFileService;

    @GetMapping
    public ResponseEntity<ChartDtoOnDashboard> getChartDtoOnDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chartFileService.getChartDtoOnDashboard(user.getId()));
    }
}
