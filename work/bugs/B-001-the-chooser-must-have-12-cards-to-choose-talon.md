# B-001: The chooser must have 12 cards to choose talon

- Parent: F-007
- Status: Done
- Owner: engine
- Related modules: engine

## Problem

After chooser selected trump, they immediately had 10 cards and trump was returned to hand.
The correct flow per rules: chooser should have 12 cards total (11 in hand + 1 trump on desk)
before discarding 2 cards to form the talon.

## Expected Behavior

Correct sequence per rules:
1. Chooser gets 7 cards (Phase A)
2. Chooser places trump face-down on desk → 6 in hand + 1 on desk
3. Chooser receives 5 more cards (pending 3 + talon 2) → 11 in hand + 1 on desk = **12 total**
4. Chooser discards 2 cards to form new talon → 9 in hand + 1 on desk
5. Asks "barva?" - others can say "Good" or take talon for Slam/Misère
6. After "Good": trump revealed and returns to hand → 10 in hand

## Root Cause

`chooseTrumpReducer` only added pending cards (3) to hand, not the existing talon (2).
Trump card was immediately returned to hand instead of staying on desk until after discard.

## Fix

Updated `chooseTrumpReducer`:
- Add BOTH pending cards (3) AND talon (2) to chooser's hand
- Keep trump card on desk (not in hand)
- Set talon to empty (will be recreated by discard)
- Set dealing phase to PHASE_B (not COMPLETE)

Updated `exchangeTalonReducer`:
- After discard, return trump card to hand if it was on desk

## Result

- Chooser now has 11 cards in hand + 1 trump on desk = 12 total after selecting trump
- Talon is empty after trump selection (picked up by chooser)
- After discarding 2 cards, chooser has 9 in hand + trump returns = 10 cards
- New talon contains the 2 discarded cards

## Steps to reproduce
- Start game
- Deal

## Actual behavior
- Chooser has 10 cards after selecting trump

## Verification

- All 51 engine tests pass
- New test `full flow - choose trump then discard returns trump to hand` verifies complete sequence
- docs/rules/R-003-dealing.md and docs/rules/R-004-talon.md already document correct flow
