# T-012: Web UI – paused deal, trump selection, and pending cards

- Parent: F-007
- Status: Done
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

## Result

Updated web UI to support paused dealing and trump selection:

**Types updated (`types.ts`):**
- Added `DealingPhase` and `DealingDto` types
- Added `trumpCard` and `dealing` to `GameResponse`
- Added `ChooserDecisionType` and `DecisionResponse` types
- Updated `GameType` to use English names (GAME, SEVEN, etc.)
- Added `ChooseTrumpAction` and `ChooserPassAction` action types

**GamePage updated (`GamePage.tsx`):**
- Added `CardView` component with click handling and selection highlight
- Added decision state loading from `/games/{id}/decision` endpoint
- Shows "Your Decision" panel for chooser with instructions
- Shows "Waiting for chooser" message for other players
- Chooser can click card to select, then click "Declare Trump"
- Chooser can click "Pass" to proceed to bidding
- Shows trump card prominently in game info after selection
- Shows pending cards count with face-down card visualization
- Hand is shown during dealing pause for chooser

## Verification

- Web UI builds successfully (`npm run build`)
- TypeScript compiles without errors
