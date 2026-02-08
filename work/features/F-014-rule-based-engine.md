# F-014: Rule-Based Engine and Multi-Game Support

## Summary
Redesign the game engine to use a rule-based "Action Provider" pattern. This centralizes all game logic in the engine, allowing the server to tell clients exactly what actions are possible at any moment.

## Description
This feature introduces a shift from a hardcoded Mariash engine to a generic, rule-set driven architecture. By implementing a `RuleSet` interface, the engine can generate all valid `GameAction`s for a specific player in any given state. This "Action Provider" pattern eliminates the need for clients to duplicate rule logic, as they can simply render the possibilities provided by the server. It also enables support for multiple game types and versions within the same engine framework. Additionally, the existing "Fancy" Web UI will be migrated to this new model to ensure consistency.

## Goals
- Move all validation and possibility-generation logic into a `RuleSet` interface.
- Support multiple games (Mariash, Prší, etc.) and versions via a `RuleSetRegistry`.
- Simplify clients by making them "data-driven" (rendering actions provided by the server).

## Scope
- Define `RuleSet` and `RuleSetRegistry` in the engine.
- Implement Mariash-specific `possibleActions` logic.
- Update Server API to include `possibleActions` in game responses.
- Decouple `GameState` from Mariash-specific fields.

## Tasks
- [ ] T-043: Define RuleSet interface and Registry
- [ ] T-044: Implement possibleActions for Mariash trick-taking
- [ ] T-045: GameState refactoring for polymorphism/generics
- [ ] T-046: Update Server API for action delivery
- [ ] T-050: Migrate existing "Fancy" Web UI to Rule-Based API
