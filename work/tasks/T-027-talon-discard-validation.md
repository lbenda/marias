# T-027: Validate Talon Discard Restrictions

- Parent: F-010
- Status: Done
- Owner: engine
- Related modules: engine
- Depends on: T-024 (Done - MISERE/SLAM enum names available)

## Goal
Implement validation that prevents discarding Aces and Tens to the talon,
except in Misere and Slam contracts.

## Background
From docs/RULES.md:
> **Aces and Tens cannot be discarded** to the talon (major violation)
> Exception: Misere and Slam contracts allow any cards to be discarded

Current `exchangeTalonReducer()` only validates that exactly 2 cards are discarded.

## Scope

### Add validation in GameRules
```kotlin
fun validateTalonDiscard(cards: List<Card>, gameType: GameType): Boolean {
    // Misere and Slam allow any discard
    if (gameType == GameType.MISERE || gameType == GameType.SLAM) {
        return true
    }

    // Check for Ace or Ten
    return cards.none { it.rank == Rank.ACE || it.rank == Rank.TEN }
}
```

### Update exchangeTalonReducer
- Call validation before allowing exchange
- Return error action if validation fails

### Error Handling
- New error type: `INVALID_TALON_DISCARD`
- Message: "Cannot discard Ace or Ten to talon"

## Files to Modify
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/GameRules.kt`
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducer.kt`
- Related test files

## Test Cases
1. Discard two low cards (7, 8) → allowed
2. Discard Ace in Game contract → rejected
3. Discard Ten in Hundred contract → rejected
4. Discard Ace in Misere → allowed
5. Discard Ten in Slam → allowed
6. Discard Ace + Ten in Misere → allowed

## Related Tasks
- T-009: Deal pattern validation (validates card distribution, not discard content)

## Definition of Done
- Validation prevents Ace/Ten discard in normal games
- Misere and Slam exempt from restriction
- Error returned with clear message
- Unit tests cover all cases

## Result

Implemented talon discard validation:

- Added `isValidTalonDiscard(cards, gameType)` function in `GameRules.kt`
- Returns true if cards contain no Ace or Ten, OR if gameType is MISERE/SLAM
- Updated `ExchangeTalon` validation to call `isValidTalonDiscard`
- Error message: "Cannot discard Ace or Ten to talon"
- Created `TalonValidationTest.kt` with 8 tests:
  - Unit tests for `isValidTalonDiscard` function
  - Integration tests for exchange talon with low cards, Ace, Ten

## Verification

- All 61 engine tests pass
- Validation correctly blocks Ace/Ten in normal games
- Validation correctly allows any cards in Misère/Slam
