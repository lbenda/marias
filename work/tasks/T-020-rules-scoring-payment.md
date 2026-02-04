# T-020: Rules - Scoring and Payment

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014, T-017

## Goal
Document scoring and payment rules in docs/RULES.md.

## Scope
From Article I of source:

### Base Rates (with 0.20 base)
| Game Type | Rate |
|-----------|------|
| game | 0.20 |
| seven | 0.40 |
| hundred | 0.80 |
| misère | 3.00 |
| slam | 6.00 |
| two sevens | 8.00 |
| silent seven | 0.20 |
| mistake | 1.20 |
| minor penalty | 2.00 |
| major penalty | 10.00 |
| limit | 100.00 |
| raised limit | 150.00 |

### Point Counting
- Total deck: 120 points
- Declarer's points = tricks + talon discards
- Win conditions:
  - Game: >50 points (at least 51)
  - Hundred: ≥100 points
  - Misère: 0 tricks taken
  - Slam: all 10 tricks taken

### Payment Rules
- Declarer vs all defenders (rule 2)
- Sitting out player pays/receives as defender
- Red trump doubles all rates
- Doubling multiplies rates

### Limits
- Standard limit: 100x base
- Raised limit (both defenders double): 150x base
- Lay-down hand: non-doublers pay only base rate (rule 18)

### Premium Points (PB)
- Tournament scoring system
- Games worth 50+ PB must be recorded
- All involved players must sign score sheet

## Definition of Done
- Scoring rules complete in docs/RULES.md
- Payment table documented
- Win conditions for each contract
- Limit rules explained
