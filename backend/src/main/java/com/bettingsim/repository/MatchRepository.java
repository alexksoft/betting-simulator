package com.bettingsim.repository;

import com.bettingsim.model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final DynamoDbTable<Match> table;

    public void save(Match match) {
        table.putItem(match);
    }

    public Optional<Match> findById(String matchId) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(matchId).build()));
    }

    public List<Match> findAll() {
        return table.scan(ScanEnhancedRequest.builder().build())
                .items().stream().toList();
    }

    public List<Match> findByStatus(String status) {
        return findAll().stream()
                .filter(m -> status.equals(m.getStatus()))
                .toList();
    }
}
