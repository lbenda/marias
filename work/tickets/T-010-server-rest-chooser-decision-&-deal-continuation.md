# T-010: Server REST for chooser decision and deal continuation

- Status: Planned  
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

POST /games/{id}/decision
Request:
- decisionType
- payload

## Definition of Done
- REST mirrors engine decision gate accurately
- Server refuses invalid or out-of-phase decisions
- Integration test covers full pause → decision → resume flow
