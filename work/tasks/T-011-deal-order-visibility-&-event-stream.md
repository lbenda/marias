# T-011: Deal order visibility and deal event stream

- Parent: F-007
- Status: Done
- Owner: engine / server
- Related modules: engine, server
- Depends on: T-007

## Summary of Changes

- Removed `.sorted()` calls from GameReducer — hands now preserve deal order
- Added `ReorderHand` action to engine for players to reorder their hand
- Added validation: cards must match current hand exactly (same cards, different order)
- Added `PUT /games/{id}/players/{playerId}/hand` endpoint to server
- Added `ReorderHandRequest` DTO
- Updated docs/API.md with PUT hand endpoint and reorderhand action
- Updated docs/api-tests.http with PUT hand and reorderhand examples
- Added 4 new tests: deal order preservation, reorder hand success, reorder failures

## Result

- Hands are returned in deal order initially (not sorted)
- Players can reorder their hand via PUT endpoint or `reorderhand` action
- Validation rejects mismatched cards with clear error messages
- Deal order log (`dealing.dealOrder`) captures exact sequence per player

## Verification

- All 40 engine tests pass
- Server compiles successfully

## Goal
Make deal order observable so clients can animate or analyze card dealing.

The hand is return in the order of dealing the card.

Player can modify order of card in hand. Then hand is return in the order which player choosen. The PUT method of hand,
will store the card in order which client choose, but there is validation only card which are in hand, can be put back
in different order.

## Context
In mariash, skilled players infer card distribution from deal order.
The engine must preserve and expose this information.
Players also organize their hand for easier play — the server must support custom hand ordering.

## Scope

IN:
- Per-player deal log:
    - card
    - deal index
    - phase (A/B)
- Hand ordering:
    - Hand returned in deal order by default (not sorted alphabetically)
    - Player can reorder hand via PUT endpoint
    - Validation: only cards currently in hand can be reordered
- Engine events:
    - CardDealt
    - DealPaused
    - DealResumed
- Server endpoints:
    - GET `/games/{id}/players/{playerId}/hand` — returns hand in current order
    - PUT `/games/{id}/players/{playerId}/hand` — reorders hand (validates cards match)

OUT:
- UI animations
- Replay viewer
- Auto-sorting by suit/rank (client responsibility)

## Definition of Done
- Deal order is deterministic and queryable
- Hand is returned in deal order initially (not sorted)
- PUT hand endpoint allows reordering with validation:
    - Returns error if cards don't match current hand
    - Persists new order for subsequent GET requests
- Events are emitted in strict chronological order
- No game logic depends on UI interpretation
- docs/API.md and docs/api-tests.http updated with PUT hand endpoint
