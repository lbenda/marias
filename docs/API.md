# Mariash Game Engine REST API

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
| GET | `/games/{id}/players/{playerId}/hand` | Get player's hand (in deal/custom order) |
| PUT | `/games/{id}/players/{playerId}/hand` | Reorder player's hand |
| GET | `/games/{id}/talon?playerId={id}` | Get talon (declarer only) |
| GET | `/games/{id}/bidding` | Get bidding state |

## Game Flow

```
WAITING_FOR_PLAYERS → DEALING → BIDDING → TALON_EXCHANGE → TRUMP_SELECTION → PLAYING → SCORING
```

### Two-Phase Dealing

When `twoPhase: true` (default), dealing pauses after the chooser (player after dealer) receives 7 cards.
The chooser can then:
- Select trump early with `choosetrump` action → becomes declarer, skips bidding
- Pass with `chooserpass` action → proceeds to normal bidding

```
DEALING (Phase A) → [chooser has 7 cards] → WAITING_FOR_TRUMP → [chooser decision] → DEALING (Phase B) → ...
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
{"type": "deal", "playerId": "p1", "twoPhase": true}
```
- `twoPhase` (optional, default: `true`): Enable two-phase dealing with pause for trump selection

### Choose Trump (during dealing pause)
```json
{"type": "choosetrump", "playerId": "p2", "card": {"suit": "HEARTS", "rank": "ACE"}}
```
Chooser places a specific card from their hand to declare trump. The card's suit becomes trump.
Only valid when `dealing.isWaitingForChooser` is `true`. Makes chooser the declarer.

The `trumpCard` is then visible to all players in the game state response.

### Chooser Pass (during dealing pause)
```json
{"type": "chooserpass", "playerId": "p2"}
```
Only valid when `dealing.isWaitingForChooser` is `true`. Proceeds to normal bidding.

### Bid
```json
{"type": "bid", "playerId": "p2", "gameType": "GAME"}
```
Game types: `GAME`, `SEVEN`, `HUNDRED`, `HUNDRED_SEVEN`, `MISERE`, `SLAM`, `TWO_SEVENS`

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

### Reorder Hand
```json
{"type": "reorderhand", "playerId": "p1", "cards": [
  {"suit": "HEARTS", "rank": "ACE"},
  {"suit": "SPADES", "rank": "KING"},
  ...
]}
```
Cards must match current hand exactly (same cards, different order).

## Hand Management

### Get Hand
```http
GET /games/{id}/players/{playerId}/hand
```
Returns hand in current order (deal order initially, or custom order if reordered).

Response:
```json
{
  "hand": [{"suit": "HEARTS", "rank": "ACE"}, ...],
  "validCards": [{"suit": "HEARTS", "rank": "ACE"}, ...]
}
```

### Reorder Hand
```http
PUT /games/{id}/players/{playerId}/hand
Content-Type: application/json

{"cards": [
  {"suit": "SPADES", "rank": "KING"},
  {"suit": "HEARTS", "rank": "ACE"},
  ...
]}
```
Cards must match current hand exactly. Returns error if cards don't match.

Response (success): Same as GET hand
Response (error): `{"error": "Cards don't match current hand"}`

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

| Code | Description |
|------|-------------|
| GAME | Declarer needs >50 points |
| SEVEN | Win last trick with 7 of trumps |
| HUNDRED | Declarer needs 100+ points |
| HUNDRED_SEVEN | Combined Hundred and Seven |
| MISERE | Declarer must not win any trick |
| SLAM | Declarer must win all tricks |
| TWO_SEVENS | Control both trump 7s |

## Running

```bash
./gradlew :server:run
```
Server starts on port 8080.
