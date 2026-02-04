# T-027: Validate Talon Discard Restrictions

- Parent: F-010
- Status: Planned
- Owner: engine
- Related modules: engine
- Depends on: T-024 (vocabulary - for MISERE/SLAM enum names)

## Goal
Implement validation that prevents discarding Aces and Tens to the talon,
except in Misère and Slam contracts.

## Background
From docs/RULES.md:
> **Aces and Tens cannot be discarded** to the talon (major violation)
> Exception: Misère and Slam contracts allow any cards to be discarded

Current `exchangeTalonReducer()` only validates that exactly 2 cards are discarded.

## Scope

### Add validation in GameRules
```kotlin
fun validateTalonDiscard(cards: List<Card>, gameType: GameType): Boolean {
    // Misère and Slam allow any discard
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
4. Discard Ace in Misère → allowed
5. Discard Ten in Slam → allowed
6. Discard Ace + Ten in Misère → allowed

## Definition of Done
- Validation prevents Ace/Ten discard in normal games
- Misère and Slam exempt from restriction
- Error returned with clear message
- Unit tests cover all cases
