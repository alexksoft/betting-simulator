package com.bettingsim.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PublicBetResponse {
    private String betId;
    private String username;
    private String betType;
    private BigDecimal stake;
    private double odds;
    private BigDecimal potentialWin;
    private String status;
    private String placedAt;
    private BigDecimal profitLoss;
}