# T-004: Engine — Redux-style state container

Status: Done
Owner: Engine

## Goal
Introduce a Redux-style state container in the engine to ensure deterministic, testable, replayable state transitions.

The engine should support:
- a single source of truth (engine state)
- actions representing player/system intents
- reducers that compute the next state as pure functions

## Context
Card games benefit from deterministic state transitions and reproducible behavior.
A Redux-style approach enables:
- unit testing reducers as pure functions
- replay / time travel
- stable state-driven UIs (web + Android)

## Scope
IN:
- Core state container structure (state + dispatch)
- Action model (sealed types / data classes or equivalent)
- Reducer pipeline (pure transitions)
- Ability to apply actions and get new state deterministically

OUT:
- Full gameplay rule set for any specific game (beyond initial baseline)
- Persistence / event store (optional future)
- Performance optimizations beyond reasonable defaults

## Result
- Engine supports deterministic state transitions using actions and reducers
- Reducers are pure and testable
- State is the single source of truth; UIs can render from state and dispatch actions
- Structure is ready for replay/logging features

## Notes
- Prefer simple action schemas and reducer boundaries (avoid over-engineering).
- Keep the state shape manageable; prefer normalized or structured state if it grows.

## Verification
- Unit tests cover reducer behavior for key actions
- Replaying the same action sequence yields identical final state
- No networking/UI dependencies introduced into engine

## Follow-ups (not part of this ticket)
- Add event logging / replay tooling
- Define game-specific action sets and reducer modules per game
- Add validation layer for illegal actions (error codes)
