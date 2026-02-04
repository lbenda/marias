# T-022: Rules - Violations and Penalties (Renonc)

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document violation (renonc) rules and penalties in docs/RULES.md.

## Scope
From Articles IV and V of source:

### Game Violations - Paušál 1 (Minor)
1. Not following suit
2. Not beating a lower card (when required)
3. Incorrect lead
4. Playing out of turn
5. Signals, hints, influencing, revealing cards to partner
6. Wrong talon card count (discovered after play starts)
7. Declarer changing/looking at talon after bidding commented
8. Playing announced Seven prematurely
9. Sitting-out player looking at others' cards
10. Looking at cards when collecting (without rights from previous game)

### Technical Violations - Paušál 1
11. Cutting and shuffling cards (playing lay-down hand)
12. Wrong talon count (discovered before play)
13. Not following doubling order
14. Not showing sevens or 4+ helpers in Two Sevens
15. Second+ dealing mistake in round
16. Sitting-out player seeing cards / showing cards to sitting-out
17. Looking at discarded cards without rights
18. Not sorting discarded cards by suit

### Game Violations - Paušál 2 (Major)
19. Discarding Ace or Ten to talon (except Betl/Durch)
20. Discarding announced Seven to talon
21. Playing player looking at others' cards or talon

### Not Violations
1. Revealing your cards to opponent
2. Doubling beyond 4th level (invalid but not penalized)
3. Non-offensive mariáš humor

### Consequences
1. Technical violation: only pays penalty to all others
2. Game violation: offending side loses regardless of play state
3. Game not continued after violation
4. Payment based on doubled state at violation time
5. For Hra/Sto: points based on "kontumačně uhrané body"
6. Only first violation counts if multiple occur

### Violation Claims
- Can be claimed until card collection begins
- Sitting-out player can also claim
- Referee decides disputed cases

## Implementation Notes
- Most violations are for tournament play
- Basic implementation: follow suit, beat when required
- Advanced: full renonc tracking (optional)

## Definition of Done
- Violation rules complete in docs/RULES.md
- All violation types listed
- Penalties documented
- Consequence rules explained
