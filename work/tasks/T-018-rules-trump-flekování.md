# T-018: Rules - Trump Selection and Flekování

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document trump selection and the flekování (doubling) system in docs/RULES.md.

## Scope

### Trump Selection
- Chooser places trump card face-down (already implemented in T-013)
- After "Dobrá" responses, trump card is revealed
- Trump card returns to hand after reveal
- Declarer announces contract with trump suit

### Flekování (Doubling System)
From Article II rules 14-18:

- Must follow clockwise order strictly (rule 14)
- Must express clearly with words, not gestures (rule 15)
- Words: "flek" or "dobrá" (good)
- Can double any announced contract component

### Doubling Levels
| Level | Czech | Multiplier |
|-------|-------|------------|
| 0 | základní | 1x |
| 1 | flek | 2x |
| 2 | re | 4x |
| 3 | tutti | 8x |
| 4 | boty | 16x (max) |

### Rules
- Contract higher than Seven doesn't need to match cards (rule 16)
- Player can double based on any card reasoning
- Maximum 4 doubles (beyond is not a violation but invalid)

### Lay-down Hand (Ložená hra)
Rules 19-22:
- If hand is unbeatable, must show and not play
- Playing a lay-down is considered "card shuffling" violation
- Ložená criteria by contract type:
  - Hra/Sto: Can't lose and won't lose any of 90 possible points
  - Sedma: Won't give up any trick
  - Dvě sedmy: Can't lose regardless of defense play
  - Betl: Can't take any trick
  - Durch: Can't lose any trick

## Implementation Features
- Flekování system needs implementation
- Lay-down detection (optional advanced feature)

## Definition of Done
- Trump selection rules complete (reference T-013)
- Flekování system documented
- Doubling levels table
- Lay-down rules documented
