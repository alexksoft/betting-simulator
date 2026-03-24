package com.bettingsim.service;

import com.bettingsim.dto.BetRequest;
import com.bettingsim.exception.AppException;
import com.bettingsim.model.Bet;
import com.bettingsim.model.BankrollHistory;
import com.bettingsim.model.Match;
import com.bettingsim.model.User;
import com.bettingsim.repository.BankrollHistoryRepository;
import com.bettingsim.repository.BetRepository;
import com.bettingsim.repository.MatchRepository;
import com.bettingsim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final BankrollHistoryRepository bankrollHistoryRepository;

    public Bet placeBet(BetRequest req, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Match match = matchRepository.findById(req.getMatchId())
                .orElseThrow(() -> new AppException("Match not found", HttpStatus.NOT_FOUND));

        if (!"UPCOMING".equals(match.getStatus()) && !"LIVE".equals(match.getStatus())) {
            throw new AppException("Match is not open for betting", HttpStatus.BAD_REQUEST);
        }
        if (user.getBankroll().compareTo(req.getStake()) < 0) {
            throw new AppException("Insufficient bankroll", HttpStatus.BAD_REQUEST);
        }

        BigDecimal potentialWin = req.getStake()
                .multiply(BigDecimal.valueOf(req.getOdds()))
                .setScale(2, RoundingMode.HALF_UP);

        Bet bet = new Bet();
        bet.setBetId(UUID.randomUUID().toString());
        bet.setUserId(userId);
        bet.setMatchId(req.getMatchId());
        bet.setBetType(req.getBetType());
        bet.setStake(req.getStake());
        bet.setOdds(req.getOdds());
        bet.setPotentialWin(potentialWin);
        bet.setStatus("PENDING");
        bet.setPlacedAt(Instant.now().toString());
        betRepository.save(bet);

        // Deduct stake from bankroll
        BigDecimal newBankroll = user.getBankroll().subtract(req.getStake());
        user.setBankroll(newBankroll);
        userRepository.save(user);
        recordBankroll(userId, newBankroll, "BET_PLACED");

        log.info("Bet placed: {} by user {} on match {}", bet.getBetId(), userId, req.getMatchId());
        return bet;
    }

    public List<Bet> getUserBets(String userId) {
        return betRepository.findByUserId(userId);
    }

    public void settleBetsForMatch(String matchId, String matchResult) {
        List<Bet> bets = betRepository.findByMatchId(matchId);
        for (Bet bet : bets) {
            if (!"PENDING".equals(bet.getStatus())) continue;

            boolean won = bet.getBetType().equals(matchResult);
            bet.setStatus(won ? "WON" : "LOST");

            if (won) {
                bet.setProfitLoss(bet.getPotentialWin().subtract(bet.getStake()));
                userRepository.findById(bet.getUserId()).ifPresent(user -> {
                    BigDecimal newBankroll = user.getBankroll().add(bet.getPotentialWin());
                    user.setBankroll(newBankroll);
                    userRepository.save(user);
                    recordBankroll(bet.getUserId(), newBankroll, "BET_WON");
                });
            } else {
                bet.setProfitLoss(bet.getStake().negate());
                recordBankroll(bet.getUserId(),
                        userRepository.findById(bet.getUserId()).map(User::getBankroll).orElse(BigDecimal.ZERO),
                        "BET_LOST");
            }
            betRepository.save(bet);
        }
        log.info("Settled {} bets for match {}", bets.size(), matchId);
    }

    private void recordBankroll(String userId, BigDecimal balance, String reason) {
        BankrollHistory h = new BankrollHistory();
        h.setUserId(userId);
        h.setTimestamp(Instant.now().toString());
        h.setBalance(balance);
        h.setReason(reason);
        bankrollHistoryRepository.save(h);
    }
}
