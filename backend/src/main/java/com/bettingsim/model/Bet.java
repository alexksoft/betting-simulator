package com.bettingsim.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

@Data
@DynamoDbBean
public class Bet {

    private String betId;
    private String userId;
    private String matchId;
    private String betType;       // HOME_WIN, DRAW, AWAY_WIN
    private BigDecimal stake;
    private Double odds;
    private BigDecimal potentialWin;
    private String status;        // PENDING, WON, LOST, VOID
    private BigDecimal profitLoss;
    private String placedAt;

    @DynamoDbPartitionKey
    public String getBetId() { return betId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "userId-index")
    public String getUserId() { return userId; }

    @DynamoDbSecondarySortKey(indexNames = "userId-index")
    public String getPlacedAt() { return placedAt; }
}
