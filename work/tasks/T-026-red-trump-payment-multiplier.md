# T-026: Implement Red Trump Payment Multiplier

- Parent: F-009
- Status: Planned
- Owner: engine
- Related modules: engine
- Depends on: T-025

## Goal
Implement the rule that red trump suits double all payment rates in scoring.

## Background
From docs/RULES.md:
> **Red suits** (Hearts, Diamonds): When chosen as trump, all payment rates are doubled.

Current `ScoringCalculator.calculateScore()` uses `gameType.baseValue` directly
without considering trump color.

## Scope

### Modify ScoringCalculator
```kotlin
fun calculateScore(state: GameState): RoundResult {
    val gameType = state.gameType!!
    val trumpSuit = state.trumpSuit

    // Base value from game type
    var value = gameType.baseValue

    // Red trump doubles the rate
    if (trumpSuit?.isRed == true) {
        value *= 2
    }

    // ... rest of scoring logic
}
```

### Edge Cases
- Misère and Slam don't have trump - no multiplier applies
- Check `requiresTrump` property on GameType

## Files to Modify
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/ScoringCalculator.kt`
- Related test files

## Test Cases
1. Game with black trump (Spades) → 1x multiplier
2. Game with red trump (Hearts) → 2x multiplier
3. Seven with Diamonds → 2x multiplier
4. Misère (no trump) → no multiplier
5. Slam (no trump) → no multiplier

## Definition of Done
- Red trump applies 2x multiplier to payment rates
- Misère/Slam unaffected (no trump)
- Unit tests for all cases
- Integration test for complete scoring
