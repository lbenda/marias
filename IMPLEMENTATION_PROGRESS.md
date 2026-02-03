# Mariáš Game Engine - Implementation Progress

## NOTE (2026-02-03):
This file is a historical progress log / milestone summary.
Active work is tracked as Markdown tickets under `work/tickets/`.
For current architecture and project map see `PROJECT_CONTEXT.md`.
For decisions see `docs/adr/`.

## Status: COMPLETE

## Branches

- `master` - Initial commit
- `feature/universal-card-deck` - English enums, universal deck types
- `feature/simplify-code` - Simplified/refactored code (current)

## Simplifications Made

### Model Layer
- **Rank enum** - Points and strength directly on enum (no separate lookup)
- **Card** - Simple data class with `points` and `strength` properties
- **Removed DeckType** - Only 32-card Mariáš deck supported
- **Top-level functions** - `createDeck()`, `createShuffledDeck()`

### State Layer
- **PlayerState** - Flat `wonCards: List<Card>` instead of nested `wonTricks`
- **TrickState** - `cards: List<Pair<String, Card>>` instead of separate PlayedCard class
- **BiddingState** - Only essential fields: `currentBid`, `bidderId`, `passedPlayers`
- **GameState** - Removed computed properties, shorter field names

### Rules Layer
- **Top-level functions** - `validate()`, `validCards()`, `determineTrickWinner()`, `dealCards()`
- **Removed MariasCardValues** - Logic moved to Rank enum
- **Removed TrickResolver class** - Just functions now

### Actions
- **Shorter type names** - `join`, `bid`, `play` instead of `join_game`, `place_bid`, `play_card`

### Server
- **Simplified DTOs** - Extension functions for mapping instead of DtoMapper object
- **Cleaner routes** - Less verbose response handling

## Project Structure

```
engine/
  model/     Suit, Rank, Card (with createDeck functions)
  state/     GamePhase, GameType, PlayerState, TrickState, BiddingState, GameState
  action/    GameAction sealed class
  reducer/   reduce() function
  store/     GameStore class
  rules/     validate, validCards, determineTrickWinner, dealCards, calculateScore

server/
  dto/       Request/Response classes with extension mappers
  service/   GameService
  routes/    gameRoutes()
  Application.kt
```

## How to Run

```bash
./gradlew :engine:test     # Run tests
./gradlew :server:run      # Start server on port 8080
```

## Documentation

- `docs/API.md` - API reference
- `docs/api-tests.http` - IntelliJ HTTP client tests

## Completed work items (tickets)
- work/tickets/T-001-install-project-structure.md
- work/tickets/T-002-engine-basic-game-management.md
- work/tickets/T-003-server-basic-api.md
- work/tickets/T-004-engine-redux-state-container.md
- work/tickets/T-005-openapi-generation-and-docs.md
