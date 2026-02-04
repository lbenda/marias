# T-023: Rules - Two Sevens Special Rules (Dvě sedmy)

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014, T-017

## Goal
Document special rules for the "Dvě sedmy" (Two Sevens) contract in docs/RULES.md.

## Scope
From Article III of source:

### Helper Cards (Pomocné)
- Non-trump sevens are "helper cards" in Two Sevens contract
- Defense player with 4+ helpers must show them after doubling ends
- Not showing is violation "cutting and shuffling"
- Game is still paid by declarer regardless

### Two Sevens and Hundred
- If playing Dvě sedmy a Sto: 4+ helpers shown = no show required
- Showing cards is violation "signals/revealing" for Sto component
- Dvě sedmy component always paid by declarer
- Even if defense commits other violations

### Special Cases
- If declarer doesn't have both sevens
- Defense showing trump or helper seven = violation for Sto

### Contract Requirements
- Declarer must capture/control both trump sevens
- Announced Seven cannot be discarded to talon
- This applies to helper seven in 2x7 with Sto as well

## Implementation Notes
- Complex contract type
- Requires tracking of both trump sevens
- Helper card rules are tournament-specific

## Definition of Done
- Two Sevens rules complete in docs/RULES.md
- Helper card rules documented
- Special cases for Dvě sedmy a Sto documented
