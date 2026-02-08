# A-010: Extensible Engine for Multiple Card Games

- Status: Accepted
- Date: 2026-02-02

## Decision
Design the engine to support adding multiple card game types with distinct rules and card sets.

## Context
The project is not limited to a single card game. The architecture should allow new games to be implemented by adding rules modules and content definitions rather than rewriting engine internals.

## Consequences

Positive
- Clear separation between core mechanics (turn handling, shuffling/dealing primitives, validation hooks) and game-specific rule sets.
- A structured way to add new games: define deck(s), initial state, legal actions, reducers, and win/terminal conditions.
- Encourages a plugin/module approach and test suites per game.

Trade-offs
- Slightly higher upfront design effort (interfaces, abstractions).
- Risk of over-generalization if requirements are unknown — mitigate by extracting commonality incrementally, guided by real second-game needs.
 
