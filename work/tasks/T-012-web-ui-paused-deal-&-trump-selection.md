# T-012: Web UI – paused deal, trump selection, and pending cards

- Status: Planned  
- Owner: web  
- Related modules: web-ui  
- Depends on: T-010, T-011

## Goal
Allow players to interact with paused dealing:
select trump and visually understand pending cards.

## Scope

IN:
- UI state "Waiting for chooser decision"
- Trump selection UI
- Visualization:
    - chooser: 7 cards in hand + 5 pending on table
    - other players: full hands
- Deal-order-aware animation

OUT:
- Advanced contract UI (Betl/Durch)
- Replay controls

## Definition of Done
- Chooser can select trump and continue game
- UI matches engine state exactly
- No hidden auto-resume behavior
