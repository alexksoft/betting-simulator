#!/bin/sh
set -e

ENDPOINT="http://dynamodb-local:8000"
REGION="us-east-1"
AWS="aws dynamodb --endpoint-url $ENDPOINT --region $REGION"

echo "⏳ Waiting for DynamoDB Local..."
RETRIES=30
until $AWS list-tables > /dev/null 2>&1 || [ $RETRIES -eq 0 ]; do
  RETRIES=$((RETRIES - 1))
  sleep 2
done
if [ $RETRIES -eq 0 ]; then echo "❌ DynamoDB Local did not start in time"; exit 1; fi
echo "✅ DynamoDB Local is ready"

# ── betting-users ──────────────────────────────────────────────
$AWS create-table \
  --table-name betting-users \
  --attribute-definitions \
    AttributeName=userId,AttributeType=S \
    AttributeName=email,AttributeType=S \
  --key-schema AttributeName=userId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[{
    "IndexName":"email-index",
    "KeySchema":[{"AttributeName":"email","KeyType":"HASH"}],
    "Projection":{"ProjectionType":"ALL"}
  }]' 2>/dev/null && echo "✅ Created betting-users" || echo "⚠️  betting-users already exists"

# ── betting-matches ────────────────────────────────────────────
$AWS create-table \
  --table-name betting-matches \
  --attribute-definitions \
    AttributeName=matchId,AttributeType=S \
    AttributeName=status,AttributeType=S \
  --key-schema AttributeName=matchId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[{
    "IndexName":"status-index",
    "KeySchema":[{"AttributeName":"status","KeyType":"HASH"}],
    "Projection":{"ProjectionType":"ALL"}
  }]' 2>/dev/null && echo "✅ Created betting-matches" || echo "⚠️  betting-matches already exists"

# ── betting-bets ───────────────────────────────────────────────
$AWS create-table \
  --table-name betting-bets \
  --attribute-definitions \
    AttributeName=betId,AttributeType=S \
    AttributeName=userId,AttributeType=S \
    AttributeName=placedAt,AttributeType=S \
  --key-schema AttributeName=betId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[{
    "IndexName":"userId-index",
    "KeySchema":[
      {"AttributeName":"userId","KeyType":"HASH"},
      {"AttributeName":"placedAt","KeyType":"RANGE"}
    ],
    "Projection":{"ProjectionType":"ALL"}
  }]' 2>/dev/null && echo "✅ Created betting-bets" || echo "⚠️  betting-bets already exists"

# ── betting-bankroll-history ───────────────────────────────────
$AWS create-table \
  --table-name betting-bankroll-history \
  --attribute-definitions \
    AttributeName=userId,AttributeType=S \
    AttributeName=timestamp,AttributeType=S \
  --key-schema \
    AttributeName=userId,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  2>/dev/null && echo "✅ Created betting-bankroll-history" || echo "⚠️  betting-bankroll-history already exists"

echo "🎯 DynamoDB tables ready"
