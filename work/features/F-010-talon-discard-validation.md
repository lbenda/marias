# F-010: Talon Discard Validation

* Type: Feature
* Status: Planned
* Source: T-016 (Dealing and Talon Rules), docs/RULES.md

## Description
Implement validation rules for discarding cards to the talon. Currently the engine
allows any cards to be discarded, but the rules restrict this.

## Background
From docs/RULES.md Talon Rules section:
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
- Return appropriate error when validation fails

## Success Criteria
- Cannot discard Ace or Ten to talon in normal games
- Misere and Slam allow any cards to be discarded
- Clear error message when validation fails
- All existing tests pass
- New tests cover validation scenarios

## Related Tasks
- T-027: Validate talon discard restrictions

## Related Documentation
- T-016: Rules - Dealing and Talon (Done)
- T-022: Rules - Violations and Penalties (references this as major violation)
