package com.bettingsim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String username;
    private String email;
    private BigDecimal bankroll;
}
