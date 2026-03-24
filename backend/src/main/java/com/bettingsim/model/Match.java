package com.bettingsim.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
public class Match {

    private String matchId;
    private String sport;
    private String homeTeam;
    private String awayTeam;
    private String startTime;
    private String status; // UPCOMING, LIVE, FINISHED
    private String result; // HOME_WIN, DRAW, AWAY_WIN, null if not finished
    private String createdBy;
    private String createdAt;
    // Odds from bookmaker feed
    private Double oddsHome;
    private Double oddsDraw;
    private Double oddsAway;

    @DynamoDbPartitionKey
    public String getMatchId() { return matchId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "status-index")
    public String getStatus() { return status; }
}
