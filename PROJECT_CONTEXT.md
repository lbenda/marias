# Project Context

This document is a compact structural map of the project.
It describes what exists, where it is, and how the system is shaped today.
It does NOT contain architectural rationale or history (see docs/adr/).

---

## Technology stack
- Core language: Kotlin
- Web UI: React
- State management (engine): Redux-style (single source of truth, actions, reducers)

---

## High-level architecture
The system is split into a pure game engine, optional server layer, and multiple UIs.
Business logic is isolated from transport and presentation layers.

---

## Modules

### engine
- Core game engine and rules
- Owns game state and deck(s)
- Uses a Redux-style state container for deterministic state transitions
- Pure logic: no UI, no networking, no platform-specific code
- Designed to be extensible for multiple card games:
  - each game defines its own rules, deck, actions, reducers, and win conditions

### Module Structure

```
engine/
  model/     Suit, Rank, Card (with createDeck functions)
  state/     GamePhase, GameType, PlayerState, TrickState, BiddingState, DealingState, ChooserDecision, GameState
  action/    GameAction sealed class
  reducer/   reduce() function
  store/     GameStore class
  rules/     validate, validCards, determineTrickWinner, dealCards, calculateScore
``` 

### server
- REST API exposing engine capabilities for client/server play
- Thin adapter over the engine
- OpenAPI is generated code-first
- Real-time communication: WebSocket with fallback to long/short polling
- HTTP API documentation:
  - docs/API.md — human-readable API reference
  - docs/api-tests.http — executable API examples
- Game rules: docs/RULES.md — mariash rules as implemented
- Key endpoints:
  - `/games` — game lifecycle (create, list, get, delete)
  - `/games/{id}/actions` — dispatch game actions
  - `/games/{id}/decision` — chooser decision during two-phase dealing
  - `/games/{id}/players/{playerId}/hand` — player hand management

### Module Structure

```
server/
  dto/       Request/Response classes with extension mappers
  service/   GameService
  routes/    gameRoutes()
  Application.kt
```

### ui/web
- React-based web UI
- Separate frontend build (npm + Vite, not part of Gradle build)
- Communicates with the system exclusively via the server REST API
- Written in TypeScript
- Renders state and dispatches actions derived from the engine model
- Purpose: fast REST-based prototype UI
  - create game
  - join player
  - start/init game
  - display player hand
  - two-phase dealing with chooser decision (trump selection or pass)
  - trump card display after reveal

### Module Structure

```
ui/web/
  package.json
  vite.config.ts
  tsconfig.json
  index.html
  .env.example
  src/
    main.tsx
    App.tsx
  api/
    client.ts REST client wrapper (fetch + error handling)
  types.ts API DTO types (mirrors server JSON)
  pages/
    HomePage.tsx create game
    JoinPage.tsx join/add player
    GamePage.tsx start/init + show player hand (card tiles)
```

### ui/android
- Android client
- Uses the engine directly for offline/local play
- UI delivery strategy is not yet finalized:
  - WebView hosting the React UI, or
  - native Android UI (e.g. Jetpack Compose)

---

## API boundaries
- Kotlin interfaces are used as explicit API boundaries:
  - between modules
  - for public services and entry points
- Interfaces act as stable contracts and context compression for AI tooling
- Internal helpers and local logic do not require interfaces

---

## Data & control flow

Typical server-based flow:
HTTP → server → engine → state update → response

Typical local (Android) flow:
UI → engine → state update → UI render

---

## Active architectural decisions
- ADR-0001: Kotlin as the core implementation language
- ADR-0002: React as the web UI technology
- ADR-0003: Redux-style state container for the engine
- ADR-0004: Module splitting (engine / server / ui)
- ADR-0005: Interfaces as API boundaries
- ADR-0006: Code-first OpenAPI strategy
- ADR-0010: Extensible engine for multiple card games
- ADR-0011: Android UI delivery strategy (pending)
- ADR-0012: Real-time communication (WebSocket → long polling → short polling)

---
