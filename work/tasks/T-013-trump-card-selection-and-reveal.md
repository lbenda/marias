# T-013: Trump card selection and reveal

- Parent: F-007
- Status: Done
- Owner: engine / server
- Related modules: engine, server
- Depends on: T-007

## Goal
Change trump selection from suit-only to card-based selection,
matching authentic mariash rules where chooser places a specific card face-down.

## Context
Current implementation: `ChooseTrump(playerId, suit)` - only suit is selected.
Authentic rules: Chooser places a specific card from hand face-down on desk.
This card determines trump suit AND is revealed to all players after "Dobrá".

The revealed trump card is strategically important - opponents know one card
from declarer's hand.

## Scope

IN:
- Change `ChooseTrump` action to accept `Card` instead of `Suit`
- Track trump card in game state (not just trump suit)
- Trump card temporarily leaves chooser's hand (placed on desk)
- After "Dobrá": trump card returns to hand, is marked as revealed
- Game state exposes `trumpCard: Card?` visible to all players
- Validation: card must be in chooser's hand

OUT:
- UI rendering of trump card
- Face-down phase visualization (handled in T-012)

## Implementation

### Engine changes
1. Modify `GameAction.ChooseTrump`:
   ```kotlin
   data class ChooseTrump(val playerId: String, val card: Card) : GameAction()
   ```

2. Add to `GameState`:
   ```kotlin
   val trumpCard: Card? = null  // The specific card used to declare trump
   ```

3. Update reducer:
   - Remove card from chooser's hand when placed
   - Store in `dealing.trumpCard` (face-down phase)
   - On "Dobrá": move to `state.trumpCard`, return to hand
   - Derive `trump` suit from `trumpCard.suit`

### Server changes
1. Update DTO for choosetrump action to accept card
2. Include `trumpCard` in game state response (after reveal)

## Summary of Changes

- Changed `GameAction.ChooseTrump` from `trump: Suit` to `card: Card`
- Added `trumpCard: Card?` to `DealingState` (tracks card during face-down phase)
- Added `trumpCard: Card?` to `GameState` (visible to all after reveal)
- Updated `GameReducer.chooseTrumpReducer`:
  - Removes trump card from hand temporarily
  - Adds pending cards to hand
  - Returns trump card to hand (after reveal)
  - Derives trump suit from card
- Added validation in `GameRules`: card must be in chooser's hand
- Updated `GameResponse` DTO to include `trumpCard`
- Updated `docs/API.md` with card-based choosetrump action
- Updated `docs/api-tests.http` with card-based example
- Added 2 new tests: card-not-in-hand validation, trump card visibility
- Updated 4 existing tests to use Card instead of Suit

## Result

- Trump selection now requires a specific card from chooser's hand
- Card's suit determines trump suit (backward compatible)
- Trump card is tracked in game state and visible to all players
- Card is returned to chooser's hand after reveal

## Verification

- All 42 engine tests pass
- Server compiles successfully

## Definition of Done
- Trump selection requires specific card, not just suit
- Trump card tracked separately from trump suit
- Card temporarily removed from hand during selection phase
- After reveal: card visible to all players, returned to hand
- Backward compatibility: derive suit from card
- Tests cover card-based trump selection flow
