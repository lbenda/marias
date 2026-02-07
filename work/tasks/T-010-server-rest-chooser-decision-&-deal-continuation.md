# T-010: Server REST for chooser decision and deal continuation

- Parent: F-007
- Status: Merged
- Owner: server
- Related modules: server
- Depends on: T-007, T-008

## Goal
Expose engine chooser-decision gates via REST in a clean, explicit API.

## Scope

IN:
- Endpoint to query game decision state:
    - GET /games/{id}/decision
- Endpoint to submit chooser decision:
    - POST /games/{id}/decision
- Endpoint to resume dealing after decision (if not implicit)

OUT:
- Web UI
- Authentication & permissions (assume trusted client)

## API Sketch

GET /games/{id}/decision
Response:
- decisionType
- chooserPlayerId
- allowedOptions
- pendingCardsCount
- trumpCard (after reveal, visible to all players)

POST /games/{id}/decision
Request:
- decisionType
- payload (for SELECT_TRUMP: card object, not just suit)

### Trump Card Visibility
- Before reveal (face-down): only chooser knows the card
- After "Good": trumpCard included in game state for all players
- The specific card (not just suit) is strategically important information

## Definition of Done
- REST mirrors engine decision gate accurately
- Server refuses invalid or out-of-phase decisions
- Trump selection accepts Card, not just Suit (see T-013)
- Game state includes trumpCard after reveal
- Integration test covers full pause → decision → reveal → resume flow

## Result

Implemented decision REST endpoints in server module:

- Added `DecisionResponse` DTO with fields: hasDecision, playerId, availableDecisions, mandatory, pendingCardsCount, trumpCard
- Added `DecisionRequest` DTO with fields: playerId, decisionType, card (optional for SELECT_TRUMP)
- Added `GameState.decisionResponse()` extension function
- Implemented `GET /games/{id}/decision` endpoint to query decision state
- Implemented `POST /games/{id}/decision` endpoint to submit decisions
- POST endpoint maps decision types to engine actions:
  - `SELECT_TRUMP` → `GameAction.ChooseTrump(playerId, card)`
  - `PASS` → `GameAction.ChooserPass(playerId)`
  - `TAKE_TALON` → returns 400 (not yet implemented)
- Updated `docs/API.md` with decision endpoint documentation
- Updated `docs/api-tests.http` with decision test requests

## Verification

- Server compiles successfully
- All server tests pass
- API documentation updated
