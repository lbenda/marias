# T-036: Validate Seven Announcement After Trump 7 Discard

- Parent: F-010
- Status: Planned
- Owner: engine
- Related modules: engine
- Depends on: T-027

## Goal
Prevent announcing Seven-variant game types if trump 7 was discarded to talon.

## Background
From docs/RULES.md:
> Announced Seven cannot be discarded to talon

This means: if the declarer discards the trump 7 to the talon, they cannot announce
game types that require the Seven (Seven, Hundred-Seven, Two-Sevens).

## Scope

### Track discarded cards
- Store which cards were discarded to talon (already in `state.talon`)
- Check if trump 7 is in talon when validating game type announcement

### Validation logic
```kotlin
fun canAnnounceGameType(gameType: GameType, trump: Suit, talon: List<Card>): Boolean {
    // Check if game type requires Seven
    val requiresSeven = gameType in listOf(GameType.SEVEN, GameType.HUNDRED_SEVEN, GameType.TWO_SEVENS)
    if (!requiresSeven) return true

    // Check if trump 7 was discarded
    val trump7 = Card(trump, Rank.SEVEN)
    return trump7 !in talon
}
```

### Apply validation
- In game type announcement phase (after "Barva?" response)
- Return error if invalid: "Cannot announce Seven - trump 7 was discarded"

## Files to Modify
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/GameRules.kt`
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducer.kt`
- Related test files

## Test Cases
1. Discard non-Seven cards, announce Seven → allowed
2. Discard trump 7, announce Game → allowed
3. Discard trump 7, announce Seven → rejected
4. Discard trump 7, announce Hundred-Seven → rejected
5. Discard trump 7, announce Hundred → allowed

## Definition of Done
- Validation prevents Seven announcement if trump 7 discarded
- Error returned with clear message
- Unit tests cover all cases
- docs/RULES.md already documents this rule
