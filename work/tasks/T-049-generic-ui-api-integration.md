# T-049: Integrate Generic UI with game-loop API

- Parent: F-015
- Status: Planned
- Owner: ui
- Related modules: ui/web

## Summary
Connect the generic UI components to the game service API, enabling a full game loop (fetch state + actions -> user input -> execute action -> refresh).

## Goal
Demonstrate end-to-end gameplay using only the generic UI and the new rule-based endpoints.

## Scope
- [ ] Create a dedicated "Generic Game" page or view in the web app.
- [ ] Implement polling or WebSocket connection to receive updated `possibleActions` from the server.
- [ ] Wire up action execution: clicking a generic control sends the POST request to `/games/{id}/actions`.
- [ ] Handle server-side validation errors by displaying them in a generic alert or notification toast.
- [ ] Verify that a complete game of Mariáš can be played using ONLY this generic UI.

## Files to Create/Modify
- `ui/web/src/pages/GenericGamePage.tsx` (new)
- `ui/web/src/hooks/useGenericGameLoop.ts` (new)

## Definition of Done
- [ ] A user can play a complete round in the generic UI without errors.
- [ ] State refresh and action execution loop is robust (handles errors, retries minimal).
- [ ] Basic UX messaging for waiting turns and errors is present.
