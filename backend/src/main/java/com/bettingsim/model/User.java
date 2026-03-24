package com.bettingsim.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@DynamoDbBean
public class User {

    private String userId;
    private String email;
    private String passwordHash;
    private String username;
    private BigDecimal bankroll;
    private String createdAt;

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "email-index")
    public String getEmail() { return email; }

    public static User create(String userId, String email, String username, String passwordHash, BigDecimal initialBankroll) {
        User u = new User();
        u.setUserId(userId);
        u.setEmail(email);
        u.setUsername(username);
        u.setPasswordHash(passwordHash);
        u.setBankroll(initialBankroll);
        u.setCreatedAt(Instant.now().toString());
        return u;
    }
}
