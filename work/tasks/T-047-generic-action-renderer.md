# T-047: Create GenericActionRenderer component

- Parent: F-015
- Status: Todo
- Owner: ui
- Related modules: ui/web

## Summary
Create a core React component that can render a list of `GameAction` objects received from the server. This component should be entirely data-driven and game-agnostic.

## Goal
Deliver a reusable component that can render any server-provided action list and handle empty/waiting states without game-specific code.

## Scope
- [ ] Create `GenericActionRenderer.tsx` in `ui/web`.
- [ ] Implement layout for displaying a list of available actions (e.g., a vertical list of buttons or a simple grid).
- [ ] Support displaying action labels or fallback to action type names if labels are missing.
- [ ] Handle "Empty State" (e.g., display "Waiting for turn..." if `possibleActions` is empty).
- [ ] Ensure the component is reusable and can be embedded in a dedicated "Debug/Simple" page.

## Files to Create/Modify
- `ui/web/src/components/GenericActionRenderer.tsx` (new)

## Definition of Done
- [ ] Component renders actions with labels or type names.
- [ ] Empty state displayed when no actions available.
- [ ] Component is integrated in a sample page and responds to prop updates.
