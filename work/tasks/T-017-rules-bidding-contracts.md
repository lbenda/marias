# T-017: Rules - Bidding Ladder and Contract Types

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document the bidding ladder and all contract types in docs/RULES.md.

## Scope
From Article I of source:

### Bidding Ladder (12 levels)
| Level | Contract (Czech) | Contract (English) |
|-------|------------------|-------------------|
| 1 | sedma | seven |
| 2 | sedma červená | red seven |
| 3 | sto | hundred |
| 4 | sto a sedma | hundred and seven |
| 5 | sto červených | red hundred |
| 6 | sto a sedma červených | red hundred and seven |
| 7 | betl | betl |
| 8 | durch | durch |
| 9 | dvě sedmy | two sevens |
| 10 | dvě sedmy a sto | two sevens and hundred |
| 11 | dvě sedmy, červená trumf | two sevens, red trump |
| 12 | dvě sedmy, červená trumf, a sto | two sevens, red trump, and hundred |

### Contract Descriptions
- **Hra (Game)**: Declarer needs >50 points
- **Sedma (Seven)**: Win last trick with trump 7
- **Sto (Hundred)**: Declarer needs 100+ points
- **Stosedma**: Both Seven and Hundred combined
- **Betl**: Declarer must take NO tricks
- **Durch**: Declarer must take ALL tricks
- **Dvě sedmy**: Both trump sevens must be captured/played correctly

### Red Trump Rule
- If trump is hearts or diamonds (red), rate is doubled
- Applies to all contracts

### Silent Achievements
- Tichá sedma: Unannounced Seven (half value of announced)
- Tiché sto: Unannounced Hundred (doubles flekovaná hra value)

### Omyl (Mistake)
- Player who bids ordinary Seven but doesn't want to play
- Pays doubled game + doubled Seven automatically (rule 17)

## Implementation Features
- F-XXX: Implement bidding ladder validation
- F-XXX: Implement all contract types

## Definition of Done
- Bidding ladder complete in docs/RULES.md
- All 12 contract levels documented
- Red trump rule explained
- Silent achievements documented
