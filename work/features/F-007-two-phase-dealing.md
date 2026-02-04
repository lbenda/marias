# F-007: Two-phase dealing with chooser decision

* Type: Feature
* Status: In Progress

## Description
Introduce mariáš-style two-phase dealing where the chooser receives
a partial hand, selects trump by placing a specific card face-down,
and then receives remaining cards.

**Trump selection mechanic (per authentic mariáš rules):**
1. Chooser places a trump card face-down on desk (card leaves hand temporarily)
2. Chooser receives 5 more cards (total 11 in hand)
3. Chooser discards 2 cards to talon (9 in hand)
4. Chooser asks "Barva?" - others say "Dobrá" or take talon for Betl/Durch
5. If "Dobrá": Chooser flips trump card, announces game type, takes card back (10 in hand)
6. Chooser leads first trick

The trump card (not just suit) is revealed to all players - this is strategically important information.

## Success Criteria
- Engine supports paused dealing with trump card placement
- Trump selection uses specific card (not just suit)
- Trump card visibility tracked in game state
- Server exposes decision endpoints with card information
- UI shows trump card to all players after reveal

## Related Tasks
- T-007 Engine + server basic support
- T-008 Generic chooser decision gate
- T-009 Deal pattern validation
- T-010 REST API cleanup
- T-011 Deal order visibility
- T-012 Web UI
- T-013 Trump card selection and reveal
