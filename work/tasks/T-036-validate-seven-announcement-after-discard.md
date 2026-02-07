# T-036: Validate Seven Announcement After Trump 7 Discard

- Parent: F-010
- Status: Merged
- Owner: engine
- Related modules: engine
- Depends on: T-027

## Goal
Prevent announcing Seven-variant game types if trump 7 was discarded to talon.

## Background
From docs/rules/R-004-talon.md:
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
- docs/rules/R-004-talon.md already documents this rule

## Result

Implemented Seven-variant announcement validation:

- Added `canAnnounceSevenVariant(gameType, trump, talon)` function in `GameRules.kt`
- Returns true if gameType is not a Seven variant (SEVEN, HUNDRED_SEVEN, TWO_SEVENS)
- Returns false if gameType is Seven variant AND trump 7 is in talon
- Updated `SelectTrump` validation to call `canAnnounceSevenVariant`
- Error message: "Cannot announce Seven - trump 7 was discarded"
- Created `SevenAnnouncementValidationTest.kt` with 8 tests:
  - Non-Seven game types always allowed
  - Seven variants allowed when trump 7 not in talon
  - Seven variants rejected when trump 7 in talon
  - Different suit 7 in talon does not block announcement
  - Handles null gameType and empty talon

## Verification

- All 69 engine tests pass
- Validation correctly blocks Seven variants when trump 7 discarded
- Validation correctly allows non-Seven game types regardless of talon
