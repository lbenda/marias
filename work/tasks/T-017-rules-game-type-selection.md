# T-017: Rules – Game Type Selection

- Parent: F-008
- Status: Merged
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document the rules for selecting the game type in auction mariash,
including turn order, override rules, and determination of the declarer.

The rules must clearly distinguish game type selection
from contract selection and bidding.

## Scope

### Game Selection Phase
- Define the Game Selection phase as a distinct phase of the game.
- Specify when the phase begins and ends.

### Available Game Types
- Trump Game
- Misère
- Slam

### Turn Order
- Chooser initiates the game type selection.
- Selection proceeds clockwise.
- Only eligible players may override the current selection.

### Game Type Ranking
- Define the strict ranking of game types:
    1. Trump Game
    2. Misère
    3. Slam

### Overriding the Game Type
- Conditions under which a player may override the current game type.
- Requirement to take and discard the talon when overriding.
- The overriding player becomes the new declarer.
- A player who has taken the talon may not override again.

### Restrictions
- A game type may only be overridden by a strictly higher-ranked type.
- A game type cannot be downgraded or changed to an equal-ranked type.
- Contracts and trump-specific rules do not apply to Misère or Slam.

### End of Game Selection
- Define when the Game Selection phase is complete.
- Specify which player is the final declarer.
- Define the transition to the next phase:
    - Contract Selection (Trump Game)
    - Bidding (Misère or Slam)

## Out of Scope
- Contract definitions (R-006)
- Bidding ladder and scoring (R-007)
- Doubling rules (double, redouble, etc.)

## Definition of Done
- Game Selection rules fully documented in `docs/rules/R-005-game-types.md`
- Clear separation between game type selection, contract selection, and bidding
- No ambiguity in override eligibility or phase transitions
