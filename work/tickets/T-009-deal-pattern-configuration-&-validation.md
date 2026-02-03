# T-009: Deal pattern configuration and validation

- Status: Planned  
- Owner: engine  
- Related modules: engine  
- Depends on: T-007

## Goal
Provide a robust, validated configuration system for deal patterns,
matching real mariáš dealing behavior.

## Context
Deal patterns can vary widely:
- 7-5 / 5-5
- 5-5 / 5-5 / 5-5
- 1-by-1 or 2-by-2
  Patterns must preserve player order and total card counts.

## Scope

IN:
- Formal DealPattern schema:
    - chunk sizes
    - player order
    - chooser preview threshold
- Validation rules:
    - total cards = 30
    - no player receives >10
    - chooser preview cards received before pause
- Clear validation errors (engine-level)

OUT:
- Dealer AI logic for choosing patterns
- UI editing of patterns

## Definition of Done
- Illegal deal patterns are rejected deterministically
- Valid patterns always lead to a valid paused or completed deal
- Tests cover edge and extreme patterns (1-by-1, uneven chunks)
