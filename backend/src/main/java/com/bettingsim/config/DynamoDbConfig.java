package com.bettingsim.config;

import com.bettingsim.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.region}") private String region;
    @Value("${aws.dynamodb.endpoint:}") private String endpoint;
    @Value("${aws.accessKeyId:}") private String accessKeyId;
    @Value("${aws.secretKey:}") private String secretKey;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(region));
        if (accessKeyId != null && !accessKeyId.isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretKey)));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }

    @Bean public DynamoDbTable<User> userTable(DynamoDbEnhancedClient c) {
        return c.table("betting-users", TableSchema.fromBean(User.class));
    }

    @Bean public DynamoDbTable<Match> matchTable(DynamoDbEnhancedClient c) {
        return c.table("betting-matches", TableSchema.fromBean(Match.class));
    }

    @Bean public DynamoDbTable<Bet> betTable(DynamoDbEnhancedClient c) {
        return c.table("betting-bets", TableSchema.fromBean(Bet.class));
    }

    @Bean public DynamoDbTable<BankrollHistory> bankrollTable(DynamoDbEnhancedClient c) {
        return c.table("betting-bankroll-history", TableSchema.fromBean(BankrollHistory.class));
    }
}
