# T-003: Server — basic REST API over engine

- Status: Done
- Owner: Server

## Goal
Expose a minimal REST API for executing basic engine operations in client/server mode.

The server should act as a thin adapter layer:
- validate and map HTTP requests to engine calls
- return the updated state / result
- keep networking concerns out of the engine

## Context
The engine already supports the baseline operations:
- creating a game
- adding players
- initializing/shuffling a deck

The server provides remote access so web UI (and other clients) can operate through HTTP.

## Scope
IN:
- REST endpoints that expose the engine's baseline operations
- Request/response mapping layer
- Basic integration wiring between server and engine

OUT:
- Authentication/authorization
- Persistence (DB)
- Sessions/matchmaking
- Production-grade observability

## Result
- Server module provides HTTP access to the engine baseline:
    - create a game
    - add players
    - shuffle/prepare deck
    - retrieve game state (if supported)
- Server stays a thin adapter with minimal business logic
- API surface is stable enough for early UI/client integration

## Documentation & examples
- docs/API.md describes the server API behavior and usage
- api-test.http contains executable example requests reflecting the current API

## Verification
- Manual smoke test:
    - start server
    - execute requests from api-test.http
    - verify responses reflect engine state changes
- Build passes and engine/server integration compiles

## Follow-ups (not part of this ticket)
- Expand endpoints to support real gameplay actions (dispatch engine actions)
- Add error model + codes for invalid operations
- Add OpenAPI generation verification in CI
