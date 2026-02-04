# T-012: Web UI – paused deal, trump selection, and pending cards

- Parent: F-007
- Status: Planned
- Owner: web
- Related modules: web-ui
- Depends on: T-010, T-011, T-013

## Goal
Allow players to interact with paused dealing:
select trump by choosing a specific card and visually understand pending cards.

## Scope

IN:
- UI state "Waiting for chooser decision"
- Trump selection UI:
    - Chooser clicks a card from hand to select as trump
    - Card shown face-down on table until reveal
    - After "Dobrá": card flips face-up, then returns to hand
- Trump card display:
    - After reveal: show trump card to all players (not just suit)
    - Highlight or display the revealed card prominently
- Visualization:
    - chooser: 7 cards in hand + trump card on table + pending cards
    - other players: full hands
- Deal-order-aware animation

OUT:
- Advanced contract UI (Betl/Durch)
- Replay controls

## Definition of Done
- Chooser can select trump by clicking a card (not just picking suit)
- Trump card shown face-down, then revealed after "Dobrá"
- All players see the revealed trump card (strategic information)
- UI matches engine state exactly
- No hidden auto-resume behavior
