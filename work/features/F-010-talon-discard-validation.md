# F-010: Talon Discard Validation

* Type: Feature
* Status: Merged
* Source: T-016 (Dealing and Talon Rules), docs/rules/R-004-talon.md

## Description
Implement validation rules for discarding cards to the talon. Currently the engine
allows any cards to be discarded, but the rules restrict this.

## Background
From docs/rules/R-004-talon.md:
> **Aces and Tens cannot be discarded** to the talon (major violation)
> Exception: Misere and Slam contracts allow any cards to be discarded
> Announced Seven cannot be discarded to talon

Current implementation gap:
- `exchangeTalonReducer()` only validates card count (must be 2)
- No validation for Ace/Ten restriction
- No game-type-specific exception handling

## Scope
- Add validation in talon exchange to reject Ace/Ten discards
- Allow exception for Misere and Slam contracts
- Validate that trump 7 discard prevents Seven-variant announcements
- Return appropriate error when validation fails

## Success Criteria
- Cannot discard Ace or Ten to talon in normal games
- Misere and Slam allow any cards to be discarded
- If trump 7 is discarded, cannot announce Seven/Hundred-Seven/Two-Sevens
- Clear error message when validation fails
- All existing tests pass
- New tests cover validation scenarios

## Related Tasks
- T-027: Validate Ace/Ten talon discard restrictions (Done)
- T-036: Validate Seven announcement after trump 7 discard (Done)
- T-037: UI for Talon Exchange (Done)

## Future Scope (not in current feature)
- Talon change timing: allow changing talon before first defender responds to "Barva?"
- Wrong card count detection and violation handling

## Related Documentation
- T-016: Rules - Dealing and Talon (Done)
- T-022: Rules - Violations and Penalties (references this as major violation)

## Result

Implemented full talon discard validation:

1. **Ace/Ten Restriction (T-027)**:
   - `isValidTalonDiscard(cards, gameType)` validates cards
   - Rejects Ace or Ten in normal games (GAME, SEVEN, HUNDRED, HUNDRED_SEVEN, TWO_SEVENS)
   - Allows any cards in Misère and Slam contracts
   - Validation integrated into `ExchangeTalon` action

2. **Seven Announcement Restriction (T-036)**:
   - `canAnnounceSevenVariant(gameType, trump, talon)` validates Seven variants
   - Rejects Seven/Hundred-Seven/Two-Sevens if trump 7 is in talon
   - Validation integrated into `SelectTrump` action

3. **Test Coverage**:
   - `TalonValidationTest.kt`: 10 tests for Ace/Ten restriction
   - `SevenAnnouncementValidationTest.kt`: 8 tests for Seven variant restriction
   - All 69 engine tests pass
