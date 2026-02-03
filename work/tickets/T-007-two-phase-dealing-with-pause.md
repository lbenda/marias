# T-007: Two-phase dealing with paused trump selection and deal-order visibility

- Status: Ready
- Owner: engine / AI
- Related modules: engine, server
- Related ADRs: (none)

## Goal
Implement configurable dealing patterns where one player may receive an initial partial hand (typically 7 cards),
the game pauses for trump selection, and then dealing continues. Players must be able to see the exact order
in which cards were dealt to them.

## Context
Currently, the engine deals 10 cards to each of 3 players in a single uninterrupted flow.

We need a new dealing rule used in mariáš:
- One player (the one immediately after the dealer) must receive an initial chunk (default 7 cards),
  then the deal pauses until that player selects trump (or a contract decision), and only then receives the remaining cards.
- The other two players may receive their cards directly into their hands while the chooser’s remaining cards stay on the table.

Additionally, dealing must be configurable to match real-world “power play” behavior:
- Dealer can choose different chunk sizes (e.g. 7-5 / 5-5 / 1-1-1...) while preserving player order.
- The key invariant is that the “chooser” may have some cards dealt face-down/on-table and must not receive them into hand
  before selecting trump/contract.

## Scope

IN:
- Engine support for **configurable dealing sequences** (chunk sizes) across players while preserving player order.
- Support for a **two-phase deal**:
    - Phase A: deal initial chunk(s) until chooser has received the required “preview” cards (default 7).
    - Pause state: engine stops dealing and waits for chooser action (select trump / decide contract).
    - Phase B: continue dealing remaining cards (chooser gets remaining cards that were held on table).
- Represent “cards on table” (pending dealt cards) distinctly from “cards in hand” for the chooser.
- Emit/record **per-player deal order** (the exact sequence of cards dealt to that player), so UI can render it.
- Add engine-level events/state transitions required by server in later tickets:
    - e.g. `DealPausedWaitingForTrump`, `TrumpSelected`, `DealResumed`, `DealCompleted`
- REST endpoints in server for use new features of engine

OUT:
- Web UI changes (will be separate ticket).
- Full ruleset decisions for Betl/Durch vs trump selection (engine should support a generic “chooser decision gate”,
  but detailed contracts beyond “select trump” will be handled later unless already existing).

## Constraints
- Must keep engine modular: avoid embedding server/UI concerns; expose required state & events only.
- Dealing must be reproducible/deterministic given a fixed deck order (important for mariáš skill play).
- Preserve player order strictly (dealer chooses chunk sizes, but order of recipients remains consistent).

## Implementation plan
1. Introduce a `DealPattern` / `DealingPlan` model:
    - Inputs: dealer seat, chooser seat (player after dealer), chunk sizes sequence (e.g. [7,5,5,5,5,5] distributed by turn),
      and rule config (previewCardsForChooser=7).
    - Validation: sums to total required cards (30), preserves allowed distribution, does not skip player order.
2. Extend engine state machine to support paused dealing:
    - Add state `DEALING_PHASE_A`, `WAITING_FOR_CHOOSER_DECISION`, `DEALING_PHASE_B`, `DEAL_COMPLETE`.
3. Add representation for “pending dealt cards”:
    - For chooser: after Phase A, remaining dealt-to-chooser cards go to `table/pending` until decision.
    - For other players: cards can go directly to hand (unless pattern says otherwise).
4. Implement dealing execution:
    - Iterate over the dealing plan step-by-step, producing:
        - game events (for server/UI),
        - per-player deal-order log,
        - updated hands and pending piles.
    - Stop exactly when chooser has received `previewCardsForChooser` into hand AND any further cards for chooser would be pending,
      then transition to waiting state.
5. Implement engine command to accept chooser decision:
    - `chooseTrump(suit)` (or generic `chooserDecision(payload)`)
    - On success, move pending chooser cards from table into chooser hand and resume dealing phase B.
6. Tests:
    - Determinism: given a known deck order, verify exact cards received and their order.
    - Multiple patterns: 7-5 / 5-5-... / 1-by-1, including edge cases.
    - Validation: illegal patterns rejected.
    - Pause/resume: ensure engine refuses “continue deal” until chooser decision is applied.
7. Minimal integration surface for server:
    - Ensure engine exposes current phase, whose turn/decision is required, and pending cards counts.

## Definition of Done
- Engine can run a game with:
    - legacy immediate deal (10/10/10) unchanged
    - new two-phase deal enabled with default rule:
        - chooser (player after dealer) gets 7 cards, then engine pauses
        - after decision, chooser receives remaining 5 and deal finishes
        - other players receive 5+5 (or equivalent from pattern) into hand
- Engine exposes:
    - current dealing phase and required chooser action when paused
    - chooser pending cards (count + exact cards internally; exposure policy decided by server/UI later)
    - per-player deal order log sufficient for UI animation/visibility
- Automated tests cover:
    - at least 3 different deal patterns (including 1-by-1)
    - pause/resume behavior
    - deterministic outcomes for a fixed deck

## Notes (optional)
- Contract ambiguity (trump vs Betl/Durch):
    - Implement the pause as a generic “chooser decision gate” in engine state, but only wire `chooseTrump` in this ticket.
    - Follow-up ticket can expand chooser options to Betl/Durch if rules require it.
