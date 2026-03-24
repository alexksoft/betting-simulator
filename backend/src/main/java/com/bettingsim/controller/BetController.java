package com.bettingsim.controller;

import com.bettingsim.dto.BetRequest;
import com.bettingsim.model.Bet;
import com.bettingsim.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bet placeBet(@Valid @RequestBody BetRequest req,
                        @AuthenticationPrincipal String userId) {
        return betService.placeBet(req, userId);
    }

    @GetMapping
    public List<Bet> getMyBets(@AuthenticationPrincipal String userId) {
        return betService.getUserBets(userId);
    }
}
