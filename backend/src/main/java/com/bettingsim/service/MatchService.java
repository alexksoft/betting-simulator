package com.bettingsim.service;

import com.bettingsim.dto.MatchRequest;
import com.bettingsim.dto.SettleMatchRequest;
import com.bettingsim.exception.AppException;
import com.bettingsim.model.Match;
import com.bettingsim.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final BetService betService;

    public Match createMatch(MatchRequest req, String userId) {
        Match match = new Match();
        match.setMatchId(UUID.randomUUID().toString());
        match.setSport(req.getSport());
        match.setHomeTeam(req.getHomeTeam());
        match.setAwayTeam(req.getAwayTeam());
        match.setStartTime(req.getStartTime());
        match.setStatus("UPCOMING");
        match.setCreatedBy(userId);
        match.setCreatedAt(Instant.now().toString());
        match.setOddsHome(req.getOddsHome());
        match.setOddsDraw(req.getOddsDraw());
        match.setOddsAway(req.getOddsAway());
        matchRepository.save(match);
        return match;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchesByStatus(String status) {
        return matchRepository.findByStatus(status);
    }

    public Match getMatch(String matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new AppException("Match not found", HttpStatus.NOT_FOUND));
    }

    public Match settleMatch(String matchId, SettleMatchRequest req) {
        Match match = getMatch(matchId);
        if ("FINISHED".equals(match.getStatus())) {
            throw new AppException("Match already settled", HttpStatus.BAD_REQUEST);
        }
        match.setStatus("FINISHED");
        match.setResult(req.getResult());
        matchRepository.save(match);
        betService.settleBetsForMatch(matchId, req.getResult());
        return match;
    }
}
