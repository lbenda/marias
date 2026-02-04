# T-009: Deal pattern configuration and validation

- Parent: F-007
- Status: Done
- Owner: engine
- Related modules: engine
- Depends on: T-007

## Summary of Changes

- Enhanced `DealPattern.validate()` with comprehensive validation:
  - Total cards must equal 32
  - Each player must receive exactly 10 cards
  - Talon must receive exactly 2 cards
  - Valid player offsets only (-1, 0, 1, 2)
  - Positive card counts required
  - Preview cards threshold must be 1-10
  - Pattern must have at least one step
  - Chooser must receive at least previewCardsForChooser cards
- Added `isValid` property for convenience
- Integrated pattern validation into `GameRules.validate()` for DealCards action
- Added 6 new validation tests

## Result

Deal patterns are now robustly validated:
- Invalid patterns are rejected during deal action with clear error messages
- All built-in patterns (STANDARD, TWO_PHASE, oneByOne) validate correctly
- Custom patterns can be used if they meet all validation rules

## Verification

- All 50 engine tests pass
- 6 new validation tests cover edge cases

## Goal
Provide a robust, validated configuration system for deal patterns,
matching real mariash dealing behavior.

## Scope

IN:
- Formal DealPattern schema:
    - chunk sizes per player ✓
    - dealing direction (clockwise) ✓
    - chooser preview threshold (default: 7) ✓
- Validation rules:
    - total cards = 32 (30 to hands + 2 to talon) ✓
    - each player receives exactly 10 cards ✓
    - chooser receives preview cards (7) before pause ✓
    - talon receives exactly 2 cards ✓
    - valid player offsets (-1, 0, 1, 2) ✓
    - positive card counts ✓
    - preview threshold 1-10 ✓
- Clear validation errors (engine-level) ✓

OUT:
- Dealer AI logic for choosing patterns
- UI editing of patterns
- Talon discard validation (see T-027)

## Files Modified

- `engine/src/main/kotlin/.../state/DealingState.kt` - enhanced validate()
- `engine/src/main/kotlin/.../rules/GameRules.kt` - pattern validation in DealCards
- `engine/src/test/kotlin/.../TwoPhaseDealingTest.kt` - 6 new tests

## Definition of Done
- Illegal deal patterns are rejected deterministically ✓
- Valid patterns always lead to a valid paused or completed deal ✓
- Tests cover edge and extreme patterns (1-by-1, uneven chunks) ✓
