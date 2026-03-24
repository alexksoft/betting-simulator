package com.bettingsim.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UserStatsResponse {
    private int totalBets;
    private int wonBets;
    private int lostBets;
    private int pendingBets;
    private double winRate;
    private BigDecimal totalStaked;
    private BigDecimal totalReturns;
    private BigDecimal profitLoss;
    private double roi;
    private BigDecimal currentBankroll;
    private List<BankrollPoint> bankrollHistory;

    @Data
    @Builder
    public static class BankrollPoint {
        private String timestamp;
        private BigDecimal balance;
        private String reason;
    }
}
