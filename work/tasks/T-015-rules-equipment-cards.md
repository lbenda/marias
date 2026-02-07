# T-015: Rules - Equipment and Card Values

- Parent: F-008
- Status: Merged
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Summary of Changes

- Added German suit names table (Leaves, Acorns, Bells, Hearts)
- Added suit colors (Black/Red) with note about red trump doubling rates
- Enhanced card values table with Notes column
- Added note about Seven's special role in Seven contract
- Added note about Queen/King marriages
- Added points per suit (30)
- Added tactical note about protecting Tens

## Result

docs/rules/R-001-equipment.md is now complete with:
- Both German and French suit names
- Red/Black suit distinction (important for doubling)
- Complete card values with strength ordering
- Strategic notes for key cards

## Verification

- docs/rules/R-001-equipment.md complete
- Consistent with VOCABULARY.md terminology

## Goal
Document equipment, deck composition, and card values in docs/rules/R-001-equipment.md.

## Scope
- Deck composition (32 cards) ✓
- Suits and ranks ✓
- Card point values ✓
- Card strength ordering ✓
- Total deck points (120) ✓

## Definition of Done
- docs/rules/R-001-equipment.md complete ✓
- Card values table accurate ✓
- Consistent with vocabulary from T-014 ✓

## Implementation Impact
This documentation revealed missing implementation:
- Suit enum lacks color property (Red/Black)
- Red trump doubling not implemented in scoring

See F-009 for implementation tasks:
- T-025: Add Suit.color property
- T-026: Implement red trump payment multiplier
