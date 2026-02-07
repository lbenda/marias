# T-037: UI for Talon Exchange

- Parent: F-010
- Status: Merged
- Owner: ui/web
- Related modules: ui/web, engine
- Depends on: T-027 (Done), T-036 (Done)

## Goal
Implement UI for the talon exchange phase where the declarer picks up talon cards
and discards 2 cards from their hand.

## Background
After winning the bidding, the declarer:
1. Picks up the 2 talon cards (added to their 10-card hand = 12 cards)
2. Selects 2 cards from their hand to discard
3. Cannot discard Ace or Ten (except in Misère/Slam)
4. Proceeds to trump selection

## Scope

### Display talon cards
- Show talon cards to declarer when in TALON_EXCHANGE phase
- Highlight that these cards are from the talon (visual distinction)

### Card selection for discard
- Allow declarer to select exactly 2 cards from their hand
- Visual feedback for selected cards (highlight, checkmark, etc.)
- Show count of selected cards (0/2, 1/2, 2/2)

### Validation feedback
- If Ace/Ten selected in normal game, show warning before submit
- Display error message from engine if validation fails
- Clear error on new selection

### Exchange action
- "Confirm Discard" button enabled when exactly 2 cards selected
- Dispatch ExchangeTalon action with selected cards
- Handle success (transition to TRUMP_SELECTION) and error states

## UI Components

### TalonExchangePanel
```tsx
interface TalonExchangePanelProps {
  talon: Card[];
  hand: Card[];
  gameType: GameType;
  onExchange: (cardsToDiscard: Card[]) => void;
  error?: string;
}
```

### Card selection state
- Track selected cards in component state
- Validate selection before enabling confirm button
- Show pre-validation warning for Ace/Ten

## Test Cases
1. Declarer sees talon cards
2. Can select 2 cards from hand
3. Cannot confirm with less than 2 cards selected
4. Warning shown when selecting Ace/Ten in normal game
5. Exchange succeeds with valid cards
6. Error displayed when engine rejects discard

## Files to Modify
- `ui/web/src/pages/GamePage.tsx`
- `ui/web/src/components/` (new components as needed)

## Definition of Done
- Declarer can view talon cards during exchange phase
- Can select and discard 2 cards
- Validation feedback shown for invalid selections
- Smooth transition to trump selection after exchange

## Result

Implemented talon exchange UI in GamePage.tsx:

1. **Types added** (`types.ts`):
   - `ExchangeTalonAction` type for exchange action
   - `SelectTrumpAction` type for trump selection
   - `TalonResponse` type for talon endpoint

2. **New state variables**:
   - `talon` - cards from talon endpoint
   - `selectedDiscards` - cards selected for discard (0-2)

3. **Talon Exchange Panel**:
   - Shows talon cards with highlight
   - Displays selection count (0/2, 1/2, 2/2)
   - Validation warning for Ace/Ten in non-Misere/Slam games
   - Confirm button enabled only when 2 valid cards selected
   - Non-declarer sees waiting message

4. **Trump Selection Panel**:
   - Four suit buttons for selecting trump
   - Shows after talon exchange in normal bidding flow

5. **Hand display updates**:
   - Cards clickable during talon exchange
   - Selected cards highlighted with gold border
   - Instruction text updated for exchange phase

## Verification

- UI builds successfully (vite build)
- Server tests pass
- Integrates with existing /talon and /actions endpoints
