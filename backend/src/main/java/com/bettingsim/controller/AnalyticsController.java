package com.bettingsim.controller;

import com.bettingsim.dto.UserStatsResponse;
import com.bettingsim.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/stats")
    public UserStatsResponse getStats(@AuthenticationPrincipal String userId) {
        return analyticsService.getStats(userId);
    }
}
