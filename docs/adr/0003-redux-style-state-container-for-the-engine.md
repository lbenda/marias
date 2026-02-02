# ADR-0003 — Redux-Style State Container for the Engine

Status: Accepted
Date: 2026-02-02

## Decision
Use a Redux-style state container within the engine for state transitions.

## Context
Card games benefit from deterministic, reproducible state transitions. A Redux-like approach (single source of truth, actions, reducers) supports debugging, testing, and “time travel” or replay features. It also helps multiple UIs (web, Android) stay consistent against the same model.

## Consequences

Positive
* Deterministic state transitions (good for rules engines).
* Easier unit tests: reducers as pure functions.
* Enables replay, logging, and debugging workflows.
* UI clients can be thin: render from state, dispatch actions.

Trade-offs
* Requires careful design of action schemas and reducer boundaries.
* Potential performance considerations if state becomes very large (usually manageable for card games with good state shaping).
 