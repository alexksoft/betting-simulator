package com.bettingsim.repository;

import com.bettingsim.model.BankrollHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BankrollHistoryRepository {

    private final DynamoDbTable<BankrollHistory> table;

    public void save(BankrollHistory entry) {
        table.putItem(entry);
    }

    public List<BankrollHistory> findByUserId(String userId) {
        return table.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build()))
                .items().stream().toList();
    }
}
