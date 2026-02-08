# T-038: Rules – Contract Selection

- Parent: F-008
- Status: Todo
- Owner: docs
- Related modules: docs
- Depends on: T-018

## Goal
Document the rules for contract selection in auction mariash.

Contract selection applies only to the Trump Game type and defines
the specific obligations of the declarer before bidding begins.

The rules must clearly separate contract selection
from game type selection and bidding.

## Scope

### Contract Selection Phase
- Define the Contract Selection phase as a distinct phase of the game.
- Specify when the phase begins (after Game Selection).
- Specify when the phase ends.

### Eligibility
- Contract selection applies only if the selected game type is Trump Game.
- In Misère or Slam games, the Contract Selection phase is skipped.

### Available Contracts
- Game
- Seven
- Hundred
- Hundred-Seven
- (Optional / variant-based: Two Sevens)

### Contract Hierarchy
- Define the relative strength of available contracts.
- Specify which contracts override others.
- Prevent downgrading or lateral changes.

### Selection Rules
- The declarer selects the contract.
- The declarer may select only one valid contract combination.
- Once selected, the contract cannot be changed.

### Restrictions
- Contracts may only be selected during the Contract Selection phase.
- Contracts do not apply to Misère or Slam.
- Silent achievements are not considered contracts.

### End of Contract Selection
- Define when the Contract Selection phase is complete.
- Specify the transition to the Bidding phase.

## Out of Scope
- Game type selection (R-005)
- Bidding ladder and scoring rules (R-007)
- Doubling rules (flek, re, etc.)
- Evaluation of fulfilled or failed contracts

## Definition of Done
- Contract Selection rules fully documented in `docs/rules/R-006-contracts.md`
- Clear contract hierarchy and selection constraints
- Unambiguous phase transition to bidding
