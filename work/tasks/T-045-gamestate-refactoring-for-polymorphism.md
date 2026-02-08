# T-045: GameState refactoring for polymorphism

- Parent: F-014
- Status: Merged
- Owner: engine
- Related modules: engine, server

## Summary
Refactor the engine state model to support multiple games via polymorphic or generic state structures while maintaining serialization stability for the server API.

## Goal
Introduce a state abstraction that allows different games to plug in their own data models without breaking existing server contracts or serialization.

## Scope
- [x] Introduce `BaseGameState` (or `GameState<TData>`) abstraction
- [x] Create `MariasState` (data class) separated from engine-generic state
- [x] Ensure serialization works with kotlinx.serialization (polymorphic)
- [x] Minimize breaking changes in existing engine reducers/rules
- [x] Document migration guidelines in `IMPLEMENTATION_PROGRESS.md`

## Files to Create/Modify
- `engine/src/main/kotlin/.../state/BaseGameState.kt` (new)
- `engine/src/main/kotlin/.../state/marias/MariasState.kt` (new)
- `engine/src/main/kotlin/.../reducer/*` (adjust for new state model)
- `server/src/main/kotlin/.../dto/*` (ensure DTOs support polymorphism)

## Definition of Done
- [x] Base abstraction introduced and used by Mariash.
- [x] Serialization verified for polymorphic state over REST/WebSocket.
- [x] No regressions in existing reducers and rule validations.
