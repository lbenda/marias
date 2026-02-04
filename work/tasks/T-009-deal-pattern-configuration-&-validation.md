# T-009: Deal pattern configuration and validation

- Parent: F-007
- Status: Planned
- Owner: engine
- Related modules: engine
- Depends on: T-007

## Goal
Provide a robust, validated configuration system for deal patterns,
matching real mariash dealing behavior.

## Context
Deal patterns define how 32 cards are distributed:
- Chooser: 7 cards (phase A) + 5 cards (phase B) = 12 total, discards 2 = 10 in hand
- Other players: 10 cards each
- Talon: 2 cards

Alternative patterns for casual play:
- 5-5 for all players
- 1-by-1 or 2-by-2 rounds

Patterns must preserve player order and total card counts.

## Scope

IN:
- Formal DealPattern schema:
    - chunk sizes per player
    - dealing direction (clockwise)
    - chooser preview threshold (default: 7)
- Validation rules:
    - total cards = 32 (30 to hands + 2 to talon)
    - each player receives exactly 10 cards
    - chooser receives preview cards (7) before pause
    - talon receives exactly 2 cards
- Clear validation errors (engine-level)

OUT:
- Dealer AI logic for choosing patterns
- UI editing of patterns
- Talon discard validation (see T-027)

## Definition of Done
- Illegal deal patterns are rejected deterministically
- Valid patterns always lead to a valid paused or completed deal
- Tests cover edge and extreme patterns (1-by-1, uneven chunks)
