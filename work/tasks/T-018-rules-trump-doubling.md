# T-018: Rules - Trump Selection and Doubling

- Parent: F-008
- Status: Todo
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document trump selection and the doubling system in docs/rules/R-003-dealing.md and docs/rules/R-005-game-types.md.

## Scope

### Trump Selection
- Chooser places trump card face-down (implemented in T-013, F-007)
- Chooser asks **"Trump?"** - others respond **"Good"** or take talon
- After all say "Good", trump card is flipped face-up
- Declarer announces game type with trump suit (e.g., "Game in Hearts")
- Trump card returns to declarer's hand
- Declarer leads first trick

### Doubling System
From Article II rules 14-18:

- Must follow clockwise order strictly (rule 14)
- Must express clearly with words, not gestures (rule 15)
- Words: "double" or "good"
- Can double any announced contract component

### Doubling Levels
| Level | Czech | English | Multiplier |
|-------|-------|---------|------------|
| 0 | základní | base | 1x |
| 1 | flek | double | 2x |
| 2 | re | redouble | 4x |
| 3 | tutti | raise | 8x |
| 4 | boty | final raise | 16x (max) |

### Rules
- Contract higher than Seven doesn't need to match cards (rule 16)
- Player can double based on any card reasoning
- Maximum 4 levels (beyond is not a violation but invalid)

### Open Hand
Rules 19-22:
- If the hand is unbeatable, must show and not play
- Playing an open hand is considered "card shuffling" violation
- Open hand criteria by contract type:
  - Game/Hundred: Can't lose and won't lose any of 90 possible points
  - Seven: Won't give up any trick
  - Two Sevens: Can't lose regardless of defense play
  - Misère: Can't take any trick
  - Slam: Can't lose any trick

## Implementation Features
- Doubling system needs implementation
- Open hand detection (optional advanced feature)

## Definition of Done
- Trump selection rules complete (reference T-013)
- Doubling system documented
- Doubling levels table
- Open hand rules documented
