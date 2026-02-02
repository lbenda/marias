# Mariáš Game Engine REST API

## Overview

The Mariáš Game Engine provides a REST API for playing the Czech card game Mariáš (Licitovaný). The engine uses Redux-like state management with immutable state and actions.

## Base URL

```
http://localhost:8080
```

## Endpoints

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Check server status |

### Game Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/games` | Create a new game |
| GET | `/games` | List all games |
| GET | `/games/{gameId}` | Get game state |
| DELETE | `/games/{gameId}` | Delete a game |

### Game Actions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/games/{gameId}/actions` | Dispatch an action |

### Player Information

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/games/{gameId}/players/{playerId}/hand` | Get player's hand and valid cards |
| GET | `/games/{gameId}/talon?playerId={id}` | Get talon (declarer only) |
| GET | `/games/{gameId}/bidding` | Get current bidding state |

## Game Flow

```
1. WAITING_FOR_PLAYERS  →  3 players join
2. DEALING              →  Cards are dealt (7+3 to each player, 2 to talon)
3. BIDDING              →  Players bid or pass
4. TALON_EXCHANGE       →  Declarer exchanges cards with talon
5. TRUMP_SELECTION      →  Declarer selects trump (for HRA/SEDMA/KILO)
6. PLAYING              →  10 tricks are played
7. SCORING              →  Points are calculated
8. FINISHED             →  Game ends or new round starts
```

## Actions

### JoinGame
```json
{
  "action": {
    "type": "join_game",
    "playerId": "player1",
    "playerName": "Alice"
  }
}
```

### StartGame
```json
{
  "action": {
    "type": "start_game",
    "playerId": "player1"
  }
}
```

### DealCards
```json
{
  "action": {
    "type": "deal_cards",
    "playerId": "player1"
  }
}
```

### PlaceBid
```json
{
  "action": {
    "type": "place_bid",
    "playerId": "player2",
    "gameType": "HRA"
  }
}
```

Game types: `HRA`, `SEDMA`, `KILO`, `BETL`, `DURCH`

### Pass
```json
{
  "action": {
    "type": "pass",
    "playerId": "player3"
  }
}
```

### ExchangeTalon
```json
{
  "action": {
    "type": "exchange_talon",
    "playerId": "player2",
    "cardsToDiscard": [
      {"suit": "ZELENE", "rank": "SEDMICKA"},
      {"suit": "KULE", "rank": "OSMICKA"}
    ]
  }
}
```

### SelectTrump
```json
{
  "action": {
    "type": "select_trump",
    "playerId": "player2",
    "trump": "SRDCE"
  }
}
```

Suits: `ZELENE`, `ZALUDY`, `KULE`, `SRDCE`

### PlayCard
```json
{
  "action": {
    "type": "play_card",
    "playerId": "player2",
    "card": {"suit": "SRDCE", "rank": "ESO"}
  }
}
```

Ranks: `SEDMICKA`, `OSMICKA`, `DEVITKA`, `DESITKA`, `SPODEK`, `SVRSEK`, `KRAL`, `ESO`

### StartNewRound
```json
{
  "action": {
    "type": "start_new_round",
    "playerId": "player1"
  }
}
```

## Card Values

| Rank | Czech Name | Points | Strength |
|------|------------|--------|----------|
| SEDMICKA | Sedmička | 0 | 1 |
| OSMICKA | Osmička | 0 | 2 |
| DEVITKA | Devítka | 0 | 3 |
| DESITKA | Desítka | 10 | 4 |
| SPODEK | Spodek | 2 | 5 |
| SVRSEK | Svršek | 3 | 6 |
| KRAL | Král | 4 | 7 |
| ESO | Eso | 11 | 8 |

Total points in deck: 120 (30 per suit × 4 suits)

## Game Types

| Type | Czech Name | Base Value | Description |
|------|------------|------------|-------------|
| HRA | Hra | 1 | Declarer needs >50 points |
| SEDMA | Sedma | 2 | Win last trick with 7 of trumps |
| KILO | Kilo | 4 | Declarer needs 100+ points |
| BETL | Betl | 5 | Declarer must not win any trick |
| DURCH | Durch | 6 | Declarer must win all tricks |

## Running the Server

```bash
./gradlew :server:run
```

Server starts on port 8080.

## Testing

Use the `api-tests.http` file in IntelliJ IDEA to run interactive API tests.
