# T-042: Web UI for game type selection and overrides

- Parent: F-013
- Status: Todo
- Owner: ui
- Related modules: ui-web
- Depends on: T-041

## Goal
Implement the user interface for selecting and overriding game types.

## Scope
- Create a dialog or selection panel for choosing game types (Trump Game, Misère, Slam).
- Implement the "Override" UI for players who are not the chooser but can take the talon.
- Show the current game type selection and the status of other players' decisions.
- Integrate with the server API for submitting choices.

## Definition of Done
- Players can select game types via the UI.
- UI correctly reflects the override possibilities based on ranking.
- Visual feedback is provided during the Game Selection phase.
