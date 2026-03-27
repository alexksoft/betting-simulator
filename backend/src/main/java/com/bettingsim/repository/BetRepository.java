package com.bettingsim.repository;

import com.bettingsim.model.Bet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BetRepository {

    private final DynamoDbTable<Bet> table;

    public void save(Bet bet) {
        table.putItem(bet);
    }

    public Optional<Bet> findById(String betId) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(betId).build()));
    }

    public List<Bet> findByUserId(String userId) {
        DynamoDbIndex<Bet> index = table.index("userId-index");
        return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build()))
                .stream().flatMap(p -> p.items().stream()).toList();
    }

    public List<Bet> findByMatchId(String matchId) {
        try {
            // Try to use GSI first
            DynamoDbIndex<Bet> index = table.index("matchId-index");
            return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(matchId).build()))
                    .stream().flatMap(p -> p.items().stream()).toList();
        } catch (Exception e) {
            // Fall back to table scan if GSI doesn't exist
            return table.scan().items().stream()
                    .filter(b -> matchId.equals(b.getMatchId()))
                    .toList();
        }
    }
}
