# T-046: Server API for action delivery and execution

- Parent: F-014
- Status: Merged
- Owner: server
- Related modules: server, engine, ui

## Summary
Expose the rule-based engine functionality through two endpoints: one to fetch the game state enriched with `possibleActions` for the requesting player, and one to execute an action. Ensure security and turn-based constraints are enforced server-side.

## Goal
Provide a secure, minimal API contract that enables any client to drive gameplay using server-provided actions only.

## Scope
- [x] GET `/games/{id}`: include `possibleActions` for the authenticated player only
- [x] POST `/games/{id}/actions`: execute a selected action
- [x] DTO design for actions (type + payload + optional label/hints)
- [x] AuthZ checks: player must belong to the game
- [x] Turn enforcement: only allow actions returned by `possibleActions`
- [x] Error model: clear error codes/messages for invalid actions
- [x] Logging/tracing of actions for debugging

## Files to Create/Modify
- `server/src/main/kotlin/.../routes/GameRoutes.kt` (modify)
- `server/src/main/kotlin/.../dto/ActionDto.kt" (new)
- `server/src/main/kotlin/.../service/GameService.kt` (modify)
- `engine/src/main/kotlin/.../rules/*` (ensure serializable actions)

## Definition of Done
- [x] GET state endpoint returns player-specific `possibleActions`.
- [x] POST action endpoint executes allowed actions and rejects others with clear errors.
- [x] Basic logging/tracing added for action requests and results.
