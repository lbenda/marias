# T-016: Rules - Dealing and Talon

- Parent: F-008
- Status: Merged
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Summary of Changes

- Added cutting rules (tournament: cut not shuffle)
- Added dealing direction (clockwise)
- Added dealing mistake rule (one free per round)
- Added complete talon rules section:
  - Ownership (belongs to no player, counts for declarer)
  - Discard restrictions (no Ace/Ten except Misère/Slam)
  - Timing rules (when talon can be changed/viewed)
  - Wrong card count handling
- Added card handling after play rules

## Result

docs/rules/R-003-dealing.md and docs/rules/R-004-talon.md are now complete with:
- Cutting vs shuffling distinction
- Two-phase deal procedure
- Comprehensive talon rules
- Card handling and cleanup rules

## Verification

- docs/rules/R-003-dealing.md and docs/rules/R-004-talon.md complete
- Consistent with VOCABULARY.md terminology

## Goal
Document dealing rules and talon handling in docs/rules/R-003-dealing.md and docs/rules/R-004-talon.md.

## Scope
From Article II of source:

### Dealing
- Cards are NOT shuffled, only cut (rule 4) ✓
- Cut at least 2 cards from top or bottom ✓
- No counting, sliding, or leafing through ✓
- Dealing direction: clockwise (rule 8) ✓
- One free dealing mistake per round (rule 9) ✓

### Cutting
- Cutter (zadák) cuts with one hand only (rule 7) ✓
- Cannot lift or raise the whole deck ✓
- Can request dealer to reshape deck ✓

### Talon Rules
- Talon can be changed only before first defender comments (rule 10) ✓
- Talon belongs to no player, must remain separate (rule 11) ✓
- Cards discarded to talon count for declarer's points ✓
- Cannot look at talon after play starts (betl/durch: never) ✓
- Wrong talon card count: can be doubled, then warned (rule 12) ✓

### Card Handling After Play
- Unplayed cards must be sorted by suit and placed face-down (rule 5) ✓
- Dealer collects cards in specific order (rule 6) ✓
- Each player keeps their own tricks (rule 11) ✓
- Trick order must not be disturbed ✓

## Definition of Done
- docs/rules/R-003-dealing.md complete ✓
- Talon rules documented ✓
- Card handling rules documented ✓

## Implementation Impact

This documentation revealed missing validation:
- Ace/Ten cannot be discarded to talon (except Misère/Slam)

See F-010 for implementation tasks:
- T-027: Validate talon discard restrictions
