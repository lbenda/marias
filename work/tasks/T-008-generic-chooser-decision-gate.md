# T-008: Generic chooser decision gate in engine

- Parent: F-007
- Status: Merged
- Owner: engine
- Related modules: engine
- Depends on: T-007

## Summary of Changes

- Created `ChooserDecision.kt` with:
  - `ChooserDecisionType` enum: `SELECT_TRUMP`, `PASS`, `TAKE_TALON`
  - `DecisionGate` data class with playerId, availableDecisions, mandatory flag
  - Factory methods: `trumpSelection()`, `talonResponse()` (for future use)
- Updated `DealingState`:
  - Added `decisionGate: DecisionGate?` field
  - Added `availableDecisions` and `canMakeDecision()` helpers
- Updated `GameReducer`:
  - Creates decision gate when entering WAITING_FOR_TRUMP phase
  - Clears decision gate when decision is made (trump or pass)
- Updated `GameRules` validation:
  - Uses decision gate to validate ChooseTrump and ChooserPass actions
  - Checks if decision type is available at current gate
- Added 3 new tests for decision gate behavior

## Result

The engine now uses a generic decision gate mechanism:
- Game pauses with a `DecisionGate` specifying who must act and what decisions are available
- Validation uses the gate to check if actions are allowed
- Gate is cleared when a decision is made
- Extensible for future contract types (Misere/Slam talon taking)

## Verification

- All 45 engine tests pass
- Backward compatible with existing actions

## Goal
Generalize the "paused deal" logic into a reusable chooser-decision gate,
so the engine can later support contracts like Misere or Slam in addition to trump selection.

## Scope

IN:
- Introduce a generic engine concept: `ChooserDecisionGate` ✓
- Gate must:
    - block game progression ✓
    - specify which player must act ✓
    - specify allowed decision types ✓
- Initial supported decisions:
    - `SELECT_TRUMP` - requires a Card (not just Suit) ✓
    - `PASS` - decline to choose trump ✓
- Engine state should expose:
    - decision type(s) available ✓
    - whether decision is mandatory or optional ✓
    - for trump selection: the placed card (face-down until reveal) ✓

OUT:
- Full Misere/Slam scoring or trick logic
- UI-level decision rendering

## Files Created/Modified

- `engine/src/main/kotlin/.../state/ChooserDecision.kt` (new)
- `engine/src/main/kotlin/.../state/DealingState.kt` (modified)
- `engine/src/main/kotlin/.../reducer/GameReducer.kt` (modified)
- `engine/src/main/kotlin/.../rules/GameRules.kt` (modified)
- `engine/src/test/kotlin/.../TwoPhaseDealingTest.kt` (3 new tests)

## Definition of Done
- Engine pauses via decision gate, not ad-hoc flags ✓
- Trump selection flows through generic decision handling ✓
- No rule logic duplicated between deal & contracts ✓
