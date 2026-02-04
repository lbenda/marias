# T-011: Deal order visibility and deal event stream

- Status: Planned  
- Owner: engine / server  
- Related modules: engine, server  
- Depends on: T-007

## Goal
Make deal order observable so clients can animate or analyze card dealing.

## Context
In mariáš, skilled players infer card distribution from deal order.
The engine must preserve and expose this information.

## Scope

IN:
- Per-player deal log:
    - card
    - deal index
    - phase (A/B)
- Engine events:
    - CardDealt
    - DealPaused
    - DealResumed
- Server exposure (read-only)

OUT:
- UI animations
- Replay viewer

## Definition of Done
- Deal order is deterministic and queryable
- Events are emitted in strict chronological order
- No game logic depends on UI interpretation
