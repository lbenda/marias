# T-041: Server API for game type selection

- Parent: F-013
- Status: Todo
- Owner: server
- Related modules: server
- Depends on: T-040

## Goal
Expose game type selection and override actions via the REST API.

## Scope
- Define request/response models for `SelectGameType` action.
- Add endpoint to submit game type selection.
- Update game state DTO to include current game type and selection phase info.
- Ensure proper authorization (only the current active player can select/override).

## Definition of Done
- REST API allows players to select and override game types.
- API correctly reflects the current phase and active player.
- Integration tests verify the end-to-end flow from engine to API.
