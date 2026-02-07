# T-048: Implement dynamic control mapping (Action -> UI Component)

- Parent: F-015
- Status: Todo
- Owner: ui
- Related modules: ui/web

## Summary
Develop a mapping system that translates `GameAction` types and payloads into appropriate UI controls (buttons, combo boxes, card selectors) in the generic client.

## Goal
Enable the generic UI to render and execute any action type with minimal or zero per-action custom code.

## Scope
- [ ] Define mapping logic:
    - Simple actions (e.g., `Pass`, `LeaveGame`) -> Standard Buttons.
    - Parameterized actions (e.g., `ChooseTrump`, `PlayCard`) -> List of buttons or a simple dropdown.
    - Complex selections (e.g., `DiscardTwo`) -> Multi-select toggle buttons.
- [ ] Implement a `GenericActionControl` wrapper that handles payload construction for different action types.
- [ ] Ensure that adding a new action type in the engine requires minimal or no changes in this mapping for basic functionality.

## Files to Create/Modify
- `ui/web/src/components/GenericActionControl.tsx` (new)
- `ui/web/src/utils/actionMapping.ts` (new)

## Definition of Done
- [ ] Mapping renders simple, parameterized, and multi-select actions.
- [ ] New action types can be supported by adding a small mapping entry (no core changes).
- [ ] Action payloads are constructed correctly and sent to the API.
