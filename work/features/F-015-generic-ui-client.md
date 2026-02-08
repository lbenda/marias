# F-015: Generic Data-Driven UI

- Type: Feature
- Status: Todo
- Source: Repository-driven project management

## Summary
Create a simplified, generic UI client that can render any game supported by the engine without knowing the specific rules. It relies entirely on the `possibleActions` provided by the server.

## Description
The Generic Data-Driven UI is a "zero-maintenance" client designed to work with any `RuleSet` without requiring ruleset-specific frontend code. It acts as a secondary, simple interface alongside the "Fancy" UI. By dynamically mapping `GameAction` objects to basic UI controls (buttons, lists, combo boxes), it allows for rapid prototyping of new games and serves as a robust fallback for debugging. This client demonstrates the full potential of the "thin client" architecture where the engine is the sole arbiter of game possibilities.

## Goals
- Rapidly support new games/rulesets with zero frontend changes.
- Provide a "fallback" or "admin" UI for testing new game logic.
- Verify the "thin client" architecture proposed in A-013.

## Scope
- Create a `GenericActionRenderer` component in React.
- Implement a dynamic menu/buttons based on the type of `GameAction`.
- Validate user choices client-side using the server's provided list of possibilities.

## Tasks
- [ ] T-047: Create GenericActionRenderer component
- [ ] T-048: Implement dynamic control mapping (Action -> UI Component)
- [ ] T-049: Integrate with game-loop API
