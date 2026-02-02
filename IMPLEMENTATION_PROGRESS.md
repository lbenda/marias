# Mariáš Game Engine - Implementation Progress

## Status: COMPLETE

## What's Done

### 1. Gradle Configuration (Complete)
- `settings.gradle.kts` - Added `:engine` and `:server` modules
- `gradle/libs.versions.toml` - Added Ktor 3.1.0, Logback, JUnit dependencies
- `~/.gradle/gradle.properties` - Set `org.gradle.java.home=C:/Users/lukbe/.jdks/openjdk-24`

### 2. Engine Module (Complete)
**Build file:** `engine/build.gradle.kts`

**Model classes:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/model/Suit.kt` - 4 suits (Zelené, Žaludy, Kule, Srdce)
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/model/Rank.kt` - 8 ranks with point values
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/model/Card.kt` - Card data class, deck creation

**State classes:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/GamePhase.kt` - Game phases enum
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/GameType.kt` - Hra, Sedma, Kilo, Betl, Durch
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/PlayerState.kt` - Player hand, tricks, score
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/TrickState.kt` - Current trick state
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/BiddingState.kt` - Bidding state
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/GameState.kt` - Complete game state

**Actions:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/action/GameAction.kt` - Sealed class with all actions

**Reducer:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducer.kt` - Redux-like reducer

**Store:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/store/GameStore.kt` - StateFlow-based store

**Rules:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/GameRules.kt` - Action validation
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/TrickResolver.kt` - Trick winner logic
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/ScoringCalculator.kt` - Round scoring
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/DeckUtils.kt` - Deck shuffle/deal

**Tests:**
- `engine/src/test/kotlin/cz/lbenda/games/marias/engine/model/CardTest.kt`
- `engine/src/test/kotlin/cz/lbenda/games/marias/engine/rules/DeckUtilsTest.kt`
- `engine/src/test/kotlin/cz/lbenda/games/marias/engine/rules/TrickResolverTest.kt`
- `engine/src/test/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducerTest.kt`
- `engine/src/test/kotlin/cz/lbenda/games/marias/engine/store/GameStoreTest.kt`

### 3. Server Module (Complete)
**Build file:** `server/build.gradle.kts`

**Files:**
- `server/src/main/kotlin/cz/lbenda/games/marias/server/Application.kt` - Ktor entry point
- `server/src/main/kotlin/cz/lbenda/games/marias/server/routes/GameRoutes.kt` - REST API routes
- `server/src/main/kotlin/cz/lbenda/games/marias/server/service/GameService.kt` - Game management
- `server/src/main/kotlin/cz/lbenda/games/marias/server/dto/GameDtos.kt` - DTOs and mappers
- `server/src/main/resources/logback.xml` - Logging config

## How to Run

### Run Tests
```bash
./gradlew :engine:test
```

### Run Server
```bash
./gradlew :server:run
```
Server starts on port 8080.

## Bug Fix Notes

Fixed bidding logic in `GameReducer.kt`:
- In `handlePlaceBid`: Was using `nextBidderId` instead of getting player at `nextBidderIndex`
- In `handlePass`: Was calculating `nextBidderIndex` with old `passedPlayers` set

The fix ensures proper player rotation during bidding phase.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /games | Create new game |
| GET | /games | List all games |
| GET | /games/{id} | Get game state |
| POST | /games/{id}/actions | Dispatch action |
| GET | /games/{id}/players/{playerId}/hand | Get player's hand |
| GET | /games/{id}/talon?playerId=X | Get talon (declarer only) |
| GET | /games/{id}/bidding | Get bidding state |
| DELETE | /games/{id} | Delete game |
| GET | /health | Health check |

## Documentation

- `documentation/API.md` - Full API documentation
- `documentation/api-tests.http` - IntelliJ IDEA HTTP client tests

## Task List Status

All tasks complete:
- All implementation tasks finished
- All tests passing
- Documentation created
