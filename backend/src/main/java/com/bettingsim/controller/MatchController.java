package com.bettingsim.controller;

import com.bettingsim.dto.MatchRequest;
import com.bettingsim.dto.SettleMatchRequest;
import com.bettingsim.model.Match;
import com.bettingsim.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/public")
    public List<Match> getAll(@RequestParam(required = false) String status) {
        return status != null ? matchService.getMatchesByStatus(status) : matchService.getAllMatches();
    }

    @GetMapping("/public/{id}")
    public Match getById(@PathVariable String id) {
        return matchService.getMatch(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Match create(@Valid @RequestBody MatchRequest req,
                        @AuthenticationPrincipal String userId) {
        return matchService.createMatch(req, userId);
    }

    @PutMapping("/{id}/settle")
    public Match settle(@PathVariable String id,
                        @Valid @RequestBody SettleMatchRequest req) {
        return matchService.settleMatch(id, req);
    }
}
