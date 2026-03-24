package com.bettingsim.service;

import com.bettingsim.dto.UserStatsResponse;
import com.bettingsim.model.Bet;
import com.bettingsim.model.BankrollHistory;
import com.bettingsim.model.User;
import com.bettingsim.repository.BankrollHistoryRepository;
import com.bettingsim.repository.BetRepository;
import com.bettingsim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final BankrollHistoryRepository bankrollHistoryRepository;

    public UserStatsResponse getStats(String userId) {
        List<Bet> bets = betRepository.findByUserId(userId);
        User user = userRepository.findById(userId).orElseThrow();

        int total = bets.size();
        int won = (int) bets.stream().filter(b -> "WON".equals(b.getStatus())).count();
        int lost = (int) bets.stream().filter(b -> "LOST".equals(b.getStatus())).count();
        int pending = (int) bets.stream().filter(b -> "PENDING".equals(b.getStatus())).count();

        BigDecimal totalStaked = bets.stream()
                .map(Bet::getStake).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReturns = bets.stream()
                .filter(b -> "WON".equals(b.getStatus()))
                .map(Bet::getPotentialWin).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = totalReturns.subtract(totalStaked
                .subtract(bets.stream().filter(b -> "PENDING".equals(b.getStatus()))
                        .map(Bet::getStake).reduce(BigDecimal.ZERO, BigDecimal::add)));

        double winRate = total > 0 ? (double) won / (won + lost) * 100 : 0;
        double roi = totalStaked.compareTo(BigDecimal.ZERO) > 0
                ? profitLoss.divide(totalStaked, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0;

        List<BankrollHistory> history = bankrollHistoryRepository.findByUserId(userId);
        List<UserStatsResponse.BankrollPoint> points = history.stream()
                .map(h -> UserStatsResponse.BankrollPoint.builder()
                        .timestamp(h.getTimestamp())
                        .balance(h.getBalance())
                        .reason(h.getReason())
                        .build())
                .toList();

        return UserStatsResponse.builder()
                .totalBets(total).wonBets(won).lostBets(lost).pendingBets(pending)
                .winRate(Math.round(winRate * 100.0) / 100.0)
                .totalStaked(totalStaked).totalReturns(totalReturns).profitLoss(profitLoss)
                .roi(Math.round(roi * 100.0) / 100.0)
                .currentBankroll(user.getBankroll())
                .bankrollHistory(points)
                .build();
    }
}
