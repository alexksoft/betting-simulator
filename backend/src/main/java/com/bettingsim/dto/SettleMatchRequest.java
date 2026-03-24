package com.bettingsim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleMatchRequest {
    @NotBlank private String result; // HOME_WIN, DRAW, AWAY_WIN
}
