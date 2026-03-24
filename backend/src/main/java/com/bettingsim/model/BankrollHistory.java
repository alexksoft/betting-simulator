package com.bettingsim.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

@Data
@DynamoDbBean
public class BankrollHistory {

    private String userId;
    private String timestamp;
    private BigDecimal balance;
    private String reason; // BET_PLACED, BET_WON, BET_LOST, DEPOSIT

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }

    @DynamoDbSortKey
    public String getTimestamp() { return timestamp; }
}
