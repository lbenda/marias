# T-008: Generic chooser decision gate in engine

- Parent: F-007
- Status: Planned  
- Owner: engine  
- Related modules: engine
- Depends on: T-007

## Goal
Generalize the "paused deal" logic into a reusable chooser-decision gate,
so the engine can later support contracts like Betl or Durch in addition to trump selection.

## Context
T-007 introduces a paused deal waiting for trump selection.
However, mariáš rules allow multiple contract types depending on variants.
Hard-coding trump selection into dealing logic would limit extensibility.

## Scope

IN:
- Introduce a generic engine concept: `ChooserDecisionGate`
- Gate must:
    - block game progression
    - specify which player must act
    - specify allowed decision types
- Initial supported decision:
    - `SELECT_TRUMP`
- Engine state should expose:
    - decision type(s) available
    - whether decision is mandatory or optional

OUT:
- Full Betl/Durch scoring or trick logic
- UI-level decision rendering

## Implementation
- Add decision model:
    - decisionType (enum)
    - chooserSeat
    - payload schema (engine-level, not REST)
- Replace hard dependency on `chooseTrump()` with:
    - `submitChooserDecision(decision)`
- Keep backward compatibility:
    - trump selection uses this gate internally

## Definition of Done
- Engine pauses via decision gate, not ad-hoc flags
- Trump selection flows through generic decision handling
- No rule logic duplicated between deal & contracts
