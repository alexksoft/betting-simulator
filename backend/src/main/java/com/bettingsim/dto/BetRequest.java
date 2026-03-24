package com.bettingsim.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetRequest {
    @NotBlank private String matchId;
    @NotBlank private String betType; // HOME_WIN, DRAW, AWAY_WIN
    @NotNull @DecimalMin("0.01") private BigDecimal stake;
    @NotNull @DecimalMin("1.01") private Double odds;
}
