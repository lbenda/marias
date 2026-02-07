# T-046: Server API for action delivery and execution

- Parent: F-014
- Status: Todo
- Owner: server
- Related modules: server, engine, ui

## Summary
Expose the rule-based engine functionality through two endpoints: one to fetch the game state enriched with `possibleActions` for the requesting player, and one to execute an action. Ensure security and turn-based constraints are enforced server-side.

## Goal
Provide a secure, minimal API contract that enables any client to drive gameplay using server-provided actions only.

## Scope
- [ ] GET `/games/{id}`: include `possibleActions` for the authenticated player only
- [ ] POST `/games/{id}/actions`: execute a selected action
- [ ] DTO design for actions (type + payload + optional label/hints)
- [ ] AuthZ checks: player must belong to the game
- [ ] Turn enforcement: only allow actions returned by `possibleActions`
- [ ] Error model: clear error codes/messages for invalid actions
- [ ] Logging/tracing of actions for debugging

## Files to Create/Modify
- `server/src/main/kotlin/.../routes/GameRoutes.kt` (modify)
- `server/src/main/kotlin/.../dto/ActionDto.kt` (new)
- `server/src/main/kotlin/.../service/GameService.kt` (modify)
- `engine/src/main/kotlin/.../rules/*` (ensure serializable actions)

## Definition of Done
- [ ] GET state endpoint returns player-specific `possibleActions`.
- [ ] POST action endpoint executes allowed actions and rejects others with clear errors.
- [ ] Basic logging/tracing added for action requests and results.
