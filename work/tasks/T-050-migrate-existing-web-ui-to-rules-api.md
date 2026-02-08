# T-050: Migrate existing Web UI to Rule-Based API

- Parent: F-014
- Status: Done
- Owner: ui
- Related modules: ui/web, server

## Summary
Update the existing Mariash Web UI to use the new `possibleActions` from the server for validation and interaction, replacing any duplicated rule logic in the frontend.

## Goal
Ensure the existing Fancy UI remains feature-rich while delegating all rule validation to the server via the new endpoints.

## Scope
- [x] Update `ui/web/src/api` to support the new GET state with actions and POST action endpoints.
- [x] Refactor card playing logic to only allow cards present in the `PlayCard` actions from the server.
- [x] Refactor bidding and trump selection to use the server-provided options.
- [x] Remove hardcoded validation logic from the React components.
- [x] Ensure the "fancy" UI elements (card animations, desk layout) are preserved while driven by the new data model.

## Files to Create/Modify
- `ui/web/src/api/gameApi.ts` (modify)
- `ui/web/src/components/Hand.tsx` (modify)
- `ui/web/src/pages/GamePage.tsx` (modify)
- Other relevant React components in `ui/web/src/components/`

## Definition of Done
- [x] Hand UI only allows clicks on cards present in `PlayCard` actions.
- [x] Bidding/trump selection driven by server-provided options.
- [x] No duplicated rule logic remains in UI—server is authoritative.
