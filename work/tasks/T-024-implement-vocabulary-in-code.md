# T-024: Implement Vocabulary in Source Code

- Parent: F-008
- Status: Planned
- Owner: engine / server
- Related modules: engine, server
- Depends on: T-014

## Goal
Update source code to use the canonical English terminology defined in docs/VOCABULARY.md.

## Context
The vocabulary task (T-014) established English translations for Czech mariash terms.
The current codebase uses inconsistent or Czech-based naming that should be updated.

## Scope

### GameType Enum Changes
Current → New:
| Current | New | Notes |
|---------|-----|-------|
| `HRA` | `GAME` | Basic game |
| `SEDMA` | `SEVEN` | Win last trick with trump 7 |
| `KILO` | `HUNDRED` | 100+ points |
| `BETL` | `MISERE` | Take no tricks |
| `DURCH` | `SLAM` | Take all tricks |

Add new types:
- `HUNDRED_SEVEN` - Combined Hundred and Seven
- `TWO_SEVENS` - Both trump 7s controlled

### Files to Update

**Engine:**
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/state/GameType.kt`
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/reducer/GameReducer.kt`
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/rules/GameRules.kt`
- All test files using GameType

**Server:**
- `server/src/main/kotlin/cz/lbenda/games/marias/server/dto/GameDtos.kt`
- Update JSON serialization names if needed

**Documentation:**
- `docs/API.md` - Update game types reference
- `docs/api-tests.http` - Update examples

### Doubling System (Future)
When implementing doubling, use these names:
- `DOUBLE` (flek) - 2x
- `REDOUBLE` (re) - 4x
- `RAISE` (tutti) - 8x
- `FINAL_RAISE` (boty) - 16x

## Implementation Notes
- Consider backward compatibility for API consumers
- May need JSON aliases during transition
- Update all tests

## Definition of Done
- GameType enum uses English names
- All references updated
- Tests pass
- API documentation updated
- JSON serialization works correctly
