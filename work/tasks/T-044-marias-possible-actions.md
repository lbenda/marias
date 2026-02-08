# T-044: Implement possibleActions for Mariash trick-taking

- Parent: F-014
- Status: Completed
- Owner: engine
- Related modules: engine, docs

## Summary
Implement `possibleActions(state, playerId)` for the Mariash ruleset, covering all phases and player-specific availability, including turn-based and turn-independent actions, in alignment with A-013.

## Goal
Provide a complete, validated list of player-specific actions for every Mariash phase so that the UI can be fully data-driven and rules are centralized in the engine.

## Scope
- [x] Define `MariasRuleSet` implementing `GameRuleSet`
- [x] Implement phase-based action generation:
    - Dealing pause / chooser decision gate (SELECT_TRUMP, PASS)
    - Bidding (PLACE_BID, PASS) — per active player
    - Talon exchange / discard pairs (DISCARD_TWO)
    - Trump reveal / game-type announcement (DECLARE_GAME_TYPE)
    - Playing (PLAY_CARD) — obey leading/following rules
    - Marriages (DECLARE_MARRIAGE) — if applicable
    - Scoring/round transitions (START_NEW_ROUND)
- [x] Implement turn-independent actions:
    - REORDER_HAND (if allowed by Mariash rules)
    - LEAVE_GAME (always available)
- [x] Ensure `validate` aligns with generated actions
- [x] Unit tests for representative states per phase

## Files to Create/Modify
- `engine/src/main/kotlin/.../rules/marias/MariasRuleSet.kt` (new)
- `engine/src/test/kotlin/.../rules/marias/MariasRuleSetTest.kt` (new)
- docs references if any rule nuance needs clarification

## Definition of Done
- [x] MariasRuleSet implements all phases correctly.
- [x] possibleActions returns correct cards for playing phase.
- [x] possibleActions correctly handles chooser decision gate.
- [x] Unit tests cover transition between all major phases.
