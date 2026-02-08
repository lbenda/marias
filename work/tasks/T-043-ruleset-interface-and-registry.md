# T-043: Define RuleSet interface and Registry

- Parent: F-014
- Status: Todo
- Owner: engine
- Related modules: engine

## Summary
Define the foundational interfaces for the rule-based engine and the registry to manage multiple versions and types of games.

## Goal
Provide a stable, versioned abstraction to select and execute game logic per ruleset, enabling multiple game types and versions to coexist safely.

## Scope
- [ ] Define `GameRuleSet` interface:
    - `possibleActions(state, playerId)`
    - `reduce(state, action)`
    - `validate(state, action)`
- [ ] Define **Universal Card Model**:
    - `CardPosition`: `placeId` (String), `visibility` (List<PlayerId>), `order` (Int).
    - `Place`: Configuration for a location (name, capacity, ordered/unordered).
- [ ] Define `RuleSetRegistry` (mapping `ruleSetId` to `GameRuleSet` implementation).
- [ ] Define versioning naming convention: `game:variant:vX` (e.g., `marias:three-player:v1`).

## Files to Create/Modify
- `engine/src/main/kotlin/.../rules/GameRuleSet.kt` (new)
- `engine/src/main/kotlin/.../rules/RuleSetRegistry.kt` (new)

## Definition of Done
- [ ] `GameRuleSet` interface defined with `possibleActions`, `reduce`, and `validate`.
- [ ] **Universal Card Model** (Positions and Places) defined and serializable.
- [ ] `RuleSetRegistry` implemented and supports versioned rule-set IDs.
- [ ] Unit tests for registry lookup and version parsing.
