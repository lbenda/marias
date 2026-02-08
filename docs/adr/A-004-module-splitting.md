# A-004: Module splitting

- Status: Accepted
- Date: 2026-02-02

## Decision
We split the project into modules:
- engine — core game engine and rules. Owns the deck and calculates game state. No UI and no network code.
- server — REST API exposing engine capabilities for client/server play.
- ui/web — React UI that plays via the server REST API.
- ui/android — Android UI that can play using the engine directly (offline/local mode).

## Context
We are building a game application with multiple clients (web, Android) and optional client/server mode.
We want clear boundaries between pure game logic and UI/transport layers.

## Consequences
- Engine stays reusable and testable (pure logic, minimal dependencies).
- Server becomes a thin adapter layer over engine.
- Web UI depends on server API; Android can run without server.
- We must keep engine API stable and version server API when needed.
