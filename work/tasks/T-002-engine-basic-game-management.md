# T-002: Engine — basic game management

- Status: Done
- Owner: Engine

## Goal
Implement the first usable slice of the game engine:
- create a game instance
- add players
- initialize and shuffle the deck
- provide basic operations needed for server integration

## Context
The engine is the core module with pure game logic (no UI, no networking).
It should be usable both directly (Android/offline) and via server (client/server mode).

## Scope
IN:
- Game creation / initialization
- Player management (adding players to a game)
- Deck initialization and shuffling
- Engine API usable by the server module

OUT:
- Full gameplay rules and turn logic
- Persistence (DB)
- Authentication/authorization
- Multiplayer sessions / matchmaking

## Result
- Engine can create a game
- Engine can add players to a game
- Engine can prepare a deck and shuffle it
- Engine exposes a stable API surface suitable for server calls

## Notes
- Implementation follows the “engine owns state + deck” boundary.
- This ticket records the already implemented baseline; future game rules should build on top of this foundation.

## Verification
- Unit tests exist or can be added for deterministic parts (e.g. state transitions).
- Manual smoke check via server endpoints confirms engine operations are callable.

## Definition of Done
- All tasks are completed.

## Follow-ups (not part of this ticket)
- Add deterministic/replay-friendly action/reducer layer (Redux-style) if not fully in place yet
- Add game-specific rules module(s)
- Add validation and error codes for invalid operations
