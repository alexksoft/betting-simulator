package com.bettingsim.repository;

import com.bettingsim.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDbTable<User> table;

    public void save(User user) {
        table.putItem(user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(userId).build()));
    }

    public Optional<User> findByEmail(String email) {
        DynamoDbIndex<User> index = table.index("email-index");
        return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build()))
                .stream().flatMap(p -> p.items().stream()).findFirst();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
