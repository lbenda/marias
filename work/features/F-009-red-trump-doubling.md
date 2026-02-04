# F-009: Red Trump Color Multiplier

* Type: Feature
* Status: Planned
* Source: T-015 (Equipment and Card Values), docs/RULES.md

## Description
Implement the rule that red trump suits (Hearts, Diamonds) double all payment rates.
This requires adding a color property to suits and applying a 2x multiplier in scoring.

## Background
From docs/RULES.md Equipment section:
> **Red suits** (Hearts, Diamonds): When chosen as trump, all payment rates are doubled.

Current implementation gaps:
1. `Suit` enum has no `color` property (Red/Black)
2. `ScoringCalculator` doesn't apply red trump multiplier

## Scope
- Add `color` property to `Suit` enum
- Add `isRed` helper property for convenience
- Modify scoring calculation to double rates for red trump
- Update tests

## Success Criteria
- Suit has color property (Red for Hearts/Diamonds, Black for Spades/Clubs)
- Scoring applies 2x multiplier when trump is red
- All existing tests pass
- New tests cover red trump multiplier

## Related Tasks
- T-025: Add Suit.color property
- T-026: Implement red trump payment multiplier

## Related Documentation
- T-015: Rules - Equipment and Card Values (Done)
- T-020: Rules - Scoring and Payment (Planned)
