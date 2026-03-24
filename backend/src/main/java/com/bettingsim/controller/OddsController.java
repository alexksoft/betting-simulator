package com.bettingsim.controller;

import com.bettingsim.service.OddsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/odds")
@RequiredArgsConstructor
public class OddsController {

    private final OddsService oddsService;

    @GetMapping
    public List<Map<String, Object>> getAllOdds() {
        return oddsService.getAllCachedOdds();
    }

    @GetMapping("/refresh")
    public Map<String, String> refresh() {
        oddsService.refreshOdds();
        return Map.of("status", "refreshed");
    }
}
