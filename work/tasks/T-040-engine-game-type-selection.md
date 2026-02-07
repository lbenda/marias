# T-040: Engine support for game type selection and overrides

- Parent: F-013
- Status: Todo
- Owner: engine
- Related modules: engine
- Depends on: T-017

## Goal
Implement the core logic for the Game Selection phase in the game engine.

## Scope
- Implement `GameSelection` phase state.
- Implement `SelectGameType` action.
- Implement logic for game type ranking and override validation.
- Implement talon handling during overrides (moving cards from talon to player hand and requiring discard).
- Implement transition from `GameSelection` to `ContractSelection` or `Bidding`.

## Definition of Done
- Game engine correctly handles game type selection by the chooser.
- Engine correctly handles overrides by other players.
- Override rules (ranking, talon usage) are strictly enforced.
- Unit tests cover all game types and override scenarios.
