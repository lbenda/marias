# Mariáš Game Engine REST API

## Base URL
```
http://localhost:8080
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check |
| POST | `/games` | Create game |
| GET | `/games` | List games |
| GET | `/games/{id}` | Get game state |
| DELETE | `/games/{id}` | Delete game |
| POST | `/games/{id}/actions` | Dispatch action |
| GET | `/games/{id}/players/{playerId}/hand` | Get player's hand |
| GET | `/games/{id}/talon?playerId={id}` | Get talon (declarer only) |
| GET | `/games/{id}/bidding` | Get bidding state |

## Game Flow

```
WAITING_FOR_PLAYERS → DEALING → BIDDING → TALON_EXCHANGE → TRUMP_SELECTION → PLAYING → SCORING
```

## Create Game

```http
POST /games
Content-Type: application/json

{"playerId": "p1", "playerName": "Alice"}
```

## Actions

All actions are sent to `POST /games/{id}/actions` with body `{"action": {...}}`.

### Join
```json
{"type": "join", "playerId": "p2", "playerName": "Bob"}
```

### Leave
```json
{"type": "leave", "playerId": "p2"}
```

### Start
```json
{"type": "start", "playerId": "p1"}
```

### Deal
```json
{"type": "deal", "playerId": "p1"}
```

### Bid
```json
{"type": "bid", "playerId": "p2", "gameType": "HRA"}
```
Game types: `HRA`, `SEDMA`, `KILO`, `BETL`, `DURCH`

### Pass
```json
{"type": "pass", "playerId": "p3"}
```

### Exchange Talon
```json
{"type": "exchange", "playerId": "p2", "cardsToDiscard": [
  {"suit": "SPADES", "rank": "SEVEN"},
  {"suit": "DIAMONDS", "rank": "EIGHT"}
]}
```

### Select Trump
```json
{"type": "trump", "playerId": "p2", "trump": "HEARTS"}
```

### Play Card
```json
{"type": "play", "playerId": "p2", "card": {"suit": "HEARTS", "rank": "ACE"}}
```

### Declare Marriage
```json
{"type": "marriage", "playerId": "p2", "suit": "HEARTS"}
```

### New Round
```json
{"type": "newround", "playerId": "p1"}
```

## Card Reference

### Suits
`SPADES` ♠, `CLUBS` ♣, `DIAMONDS` ♦, `HEARTS` ♥

### Ranks (32-card deck)
| Rank | Symbol | Points | Strength |
|------|--------|--------|----------|
| SEVEN | 7 | 0 | 1 |
| EIGHT | 8 | 0 | 2 |
| NINE | 9 | 0 | 3 |
| TEN | 10 | 10 | 4 |
| JACK | J | 2 | 5 |
| QUEEN | Q | 3 | 6 |
| KING | K | 4 | 7 |
| ACE | A | 11 | 8 |

Total deck points: 120

## Game Types

| Type | Description |
|------|-------------|
| HRA | Declarer needs >50 points |
| SEDMA | Win last trick with 7 of trumps |
| KILO | Declarer needs 100+ points |
| BETL | Declarer must not win any trick |
| DURCH | Declarer must win all tricks |

## Running

```bash
./gradlew :server:run
```
Server starts on port 8080.
