# F-013: Game type selection

* Type: Feature
* Status: Todo
* Source: R-005

## Description

Implement the Game Selection phase as defined in [R-005](../../docs/rules/R-005-game-types.md). 
This phase occurs after the chooser receives all cards and discards to the talon, but before the game proceeds to contract selection or bidding.

The phase allows players to choose the game type (Trump Game, Misère, or Slam) and potentially override each other based on a strict ranking.

### Game Type Ranking (lowest to highest)
1. Trump Game
2. Misère
3. Slam

### Key Mechanics
- **Initialization**: Chooser (first declarer) selects a game type.
- **Overrides**: Subsequent players (clockwise) can override the current selection by taking the talon, discarding, and choosing a strictly higher-ranked game type.
- **Declarer**: The last player to select/override becomes the final declarer.
- **Phase Transition**:
    - If Trump Game is selected: Proceed to Contract Selection (R-006).
    - If Misère or Slam is selected: Proceed to Bidding (R-007).

## Success Criteria
- Engine correctly manages the Game Selection phase state transitions.
- Override logic strictly follows the R-005 ranking rules.
- Talon handling during overrides is correctly implemented (take/discard).
- Server exposes endpoints for game type selection and override decisions.
- UI provides a clear interface for choosing and overriding game types.

## Related Tasks
- T-040: Engine support for game type selection and overrides
- T-041: Server API for game type selection
- T-042: Web UI for game type selection and overrides

## Related Documentation
- R-005: Game Types
- T-017: Rules – Game Type Selection (Documents the rules)
