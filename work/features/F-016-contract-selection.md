# F-016: Contract Selection and Doubling

- Type: Feature
- Status: Todo
- Source: R-006

## Description

Implement the Contract Selection phase as defined in [R-006](../../docs/rules/R-006-contract-commitments.md).
This phase occurs only after Trump Game is selected (R-005) and before playing tricks (R-007).

The phase allows the declarer to announce contract commitments (Game, Seven, Hundred, Hundred-Seven), and enables defense players to double the stakes or announce their own counter-commitments.

### Key Mechanics

- **Declarer announces**: Shows trump card and declares contract
- **Defense responds**: Each defense player (clockwise) can:
  - Accept ("Good")
  - Double any commitment (up to 16x multiplier in 4 levels)
  - Announce their own contract (defense team commitment)
- **Independent evaluation**: Contract fulfillment and game winner are separate
- **Open Hand**: Option to show cards and either auto-win or play with cards visible

### Contract Types

| Contract      | Requirement                                                    |
|---------------|----------------------------------------------------------------|
| Game          | Score more than 50 points                                      |
| Seven         | Win the last trick with a seven of trumps                      |
| Hundred       | Score at least 100 points                                      |
| Hundred-Seven | Score at least 100 points AND win last trick with seven of trumps |

### Doubling Levels

| Level | Multiplier |
|-------|------------|
| 1     | 2x         |
| 2     | 4x         |
| 3     | 8x         |
| 4     | 16x        |

## Success Criteria

- Engine correctly manages Contract Selection phase state transitions
- Declarer can announce contracts (Game/Seven/Hundred/Hundred-Seven)
- Defense players can respond in turn (Good/Double/Announce contract)
- Doubling logic correctly tracks multipliers up to 4 levels (16x max)
- Defense contract announcements tracked separately from declarer
- Prevent duplicate contracts (same contract cannot be announced by both defense players)
- Open Hand declaration and acceptance/rejection handling
- Phase transition to Playing Tricks (R-007) after all responses complete
- Server exposes endpoints for contract announcements, doubling, and responses
- UI provides clear interface for contract selection and doubling

## Related Tasks

- T-051: Engine support for contract selection and doubling
- T-052: Contract and doubling state management
- T-053: Server API for contract selection phase
- T-054: Web UI for contract selection and doubling

## Related Documentation

- R-006: Contract (Commitments)
- R-005: Game Types (prerequisite phase)
- R-007: Playing Tricks (next phase)
- R-009: Scoring (contract evaluation)
