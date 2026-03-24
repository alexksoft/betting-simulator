# 🎯 Betting Simulator - Micro-SaaS

A virtual betting simulator for punters to track and simulate bets without real money.

## Architecture

```
betting-simulator/
├── backend/          # Spring Boot (Java 17) - AWS Lambda + DynamoDB
├── frontend/         # Angular 17 - Standalone components
└── template.yaml     # AWS SAM infrastructure
```

## Tech Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| Backend        | Spring Boot 3.2, Java 17            |
| Serverless     | AWS Lambda + API Gateway (SAM)      |
| Database       | AWS DynamoDB (PAY_PER_REQUEST)      |
| Auth           | JWT (jjwt 0.12)                     |
| Frontend       | Angular 17 (standalone components)  |
| Charts         | Chart.js + ng2-charts               |
| Odds Feed      | RSS via Rome library                |

## Features

- **User Management** - Register/login with JWT, virtual £1,000 bankroll
- **Match Management** - Create matches or use existing ones (sport, teams, start time, odds)
- **Virtual Betting** - Place bets with stake, odds, bet type (HOME_WIN/DRAW/AWAY_WIN)
- **Odds Integration** - RSS feed polling every 30 minutes for bookmaker odds
- **Analytics** - Win rate, ROI, P&L, bankroll history chart

## DynamoDB Tables

| Table                    | PK         | SK          | GSI              |
|--------------------------|------------|-------------|------------------|
| betting-users            | userId     | -           | email-index      |
| betting-matches          | matchId    | -           | status-index     |
| betting-bets             | betId      | -           | userId-index     |
| betting-bankroll-history | userId     | timestamp   | -                |

## API Endpoints

### Auth (Public)
```
POST /api/auth/register   { email, username, password }
POST /api/auth/login      { email, password }
```

### Matches
```
GET  /api/matches/public          ?status=UPCOMING|LIVE|FINISHED
GET  /api/matches/public/{id}
POST /api/matches                 (auth) { sport, homeTeam, awayTeam, startTime, oddsHome, oddsDraw, oddsAway }
PUT  /api/matches/{id}/settle     (auth) { result: HOME_WIN|DRAW|AWAY_WIN }
```

### Bets
```
POST /api/bets            (auth) { matchId, betType, stake, odds }
GET  /api/bets            (auth)
```

### Analytics
```
GET  /api/analytics/stats (auth)
```

### Odds
```
GET  /api/odds            (auth) - cached bookmaker odds
GET  /api/odds/refresh    (auth) - trigger manual refresh
```

## Local Development (Docker)

### Run everything with one command
```bash
docker compose up --build
```

| Service        | URL                          |
|----------------|------------------------------|
| Frontend       | http://localhost:4200        |
| Backend API    | http://localhost:8080/api    |
| DynamoDB Local | http://localhost:8000        |

### Useful commands
```bash
# Run in background
docker compose up --build -d

# View logs
docker compose logs -f backend
docker compose logs -f frontend

# Stop and remove containers
docker compose down

# Rebuild a single service after code change
docker compose up --build backend

# Browse DynamoDB tables (requires AWS CLI)
aws dynamodb list-tables --endpoint-url http://localhost:8000 --region us-east-1
```

### Run services individually (no Docker)
```bash
# Terminal 1 — DynamoDB Local
docker run -p 8000:8000 amazon/dynamodb-local

# Terminal 2 — Backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3 — Frontend
cd frontend && npm install && npm start
```

## AWS Deployment

### Prerequisites
- AWS CLI configured
- AWS SAM CLI installed
- Java 17 + Maven

### Deploy
```bash
# 1. Build backend fat JAR
cd backend
mvn clean package -DskipTests

# 2. Deploy infrastructure + Lambda
cd ..
sam build
sam deploy --guided
# Follow prompts, note the API Gateway URL output

# 3. Update frontend environment
# Edit frontend/src/environments/environment.prod.ts
# Set apiUrl to the SAM output API Gateway URL

# 4. Build and deploy frontend to S3
cd frontend
npm run build:prod
aws s3 sync dist/betting-simulator/ s3://YOUR_BUCKET --delete

# 5. (Optional) CloudFront distribution pointing to S3 bucket
```

## Odds RSS Feed

The system polls an RSS feed every 30 minutes. Configure the URL in `application.properties`:
```properties
app.odds.rss.url=https://your-odds-rss-feed-url
```

Expected RSS entry title format:
```
Arsenal vs Chelsea - Home 1.80 Draw 3.40 Away 4.50
```

## Bet Settlement

When a match is settled via `PUT /api/matches/{id}/settle`:
1. All PENDING bets for that match are evaluated
2. Winning bets: bankroll credited with `stake × odds`
3. Losing bets: stake already deducted at placement
4. Bankroll history recorded for chart

## Security

- Passwords hashed with BCrypt
- JWT tokens expire in 24 hours
- Stateless (no sessions) - Lambda friendly
- Users can only access their own bets/stats
