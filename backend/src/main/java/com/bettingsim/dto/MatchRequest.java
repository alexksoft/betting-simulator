package com.bettingsim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchRequest {
    @NotBlank private String sport;
    @NotBlank private String homeTeam;
    @NotBlank private String awayTeam;
    @NotBlank private String startTime; // ISO-8601
    private Double oddsHome;
    private Double oddsDraw;
    private Double oddsAway;
}
