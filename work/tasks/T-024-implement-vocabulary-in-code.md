# T-024: Implement Vocabulary in Source Code

- Parent: F-008
- Status: Merged
- Owner: engine / server
- Related modules: engine, server
- Depends on: T-014

## Summary of Changes

- Renamed GameType enum values to English:
  - `HRA` → `TRUMP_GAME`
  - `SEDMA` → `SEVEN`
  - `KILO` → `HUNDRED`
  - `BETL` → `MISERE`
  - `DURCH` → `SLAM`
- Added new game types: `HUNDRED_SEVEN`, `TWO_SEVENS`
- Renamed `czechName` property to `displayName` with English values
- Renamed `fromCzechName()` to `fromName()`
- Updated ScoringCalculator with new enum values
- Updated GameReducer references
- Updated all test files
- Updated docs/API.md game types table
- Updated docs/api-tests.http examples

## Result

All GameType references now use English terminology:
- `GAME`, `SEVEN`, `HUNDRED`, `HUNDRED_SEVEN`, `MISERE`, `SLAM`, `TWO_SEVENS`

## Verification

- All 42 engine tests pass
- Documentation updated

## Goal
Update source code to use the canonical English terminology defined in docs/VOCABULARY.md.

## Scope

### GameType Enum Changes
| Old | New | Notes |
|-----|-----|-------|
| `HRA` | `GAME` | Basic game |
| `SEDMA` | `SEVEN` | Win last trick with trump 7 |
| `KILO` | `HUNDRED` | 100+ points |
| `BETL` | `MISERE` | Take no tricks |
| `DURCH` | `SLAM` | Take all tricks |
| (new) | `HUNDRED_SEVEN` | Combined Hundred and Seven |
| (new) | `TWO_SEVENS` | Both trump 7s controlled |

### Files Updated

**Engine:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/GameType.kt` ✓
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducer.kt` ✓
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/ScoringCalculator.kt` ✓
- `engine/src/test/kotlin/.../GameReducerTest.kt` ✓
- `engine/src/test/kotlin/.../TwoPhaseDealingTest.kt` ✓

**Documentation:**
- `docs/API.md` ✓
- `docs/api-tests.http` ✓

### Doubling System (Future)
When implementing doubling, use these names:
- `DOUBLE` - 2x
- `REDOUBLE` - 4x
- `RAISE` - 8x
- `FINAL_RAISE` - 16x

## Definition of Done
- GameType enum uses English names ✓
- All references updated ✓
- Tests pass ✓
- API documentation updated ✓
- JSON serialization works correctly ✓
