package com.bettingsim.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MatchStatsResponse {
    private String matchId;
    private int totalBets;
    private BigDecimal totalStaked;
    private int homeBets;
    private int drawBets;
    private int awayBets;
    private BigDecimal homeStaked;
    private BigDecimal drawStaked;
    private BigDecimal awayStaked;
}