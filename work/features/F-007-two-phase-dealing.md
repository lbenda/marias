# F-007: Two-phase dealing with chooser decision

* Type: Feature
* Status: Merged
* Source: Article II, rules 1-13 of https://www.talon.cz/pravidla/mariáš_pravidla_licitovaný_2014.pdf

## Description
Implement mariash-style two-phase dealing where the chooser receives
a partial hand (7 cards), selects trump by placing a specific card face-down,
and then receives remaining cards.

**Trump selection mechanic (per official rules):**
1. Chooser receives 7 cards (Phase A)
2. Chooser places a trump card face-down on desk (card leaves hand, 6 in hand)
3. Chooser receives 5 more cards (total 11 in hand + trump on desk = 12)
4. Chooser discards 2 cards to talon (9 in hand + trump on desk)
5. Chooser asks **"Trump?"** - others say **"Good"** or take talon for Misère/Slam
6. If "Good": Chooser flips trump card, announces game type, takes card back (10 in hand)
7. Chooser leads first trick

The trump card (not just suit) is revealed to all players - this is strategically important information.

**Card count verification:**
- After Phase A: Chooser 7, others 10 each, talon 2, pending 3 = 32
- After trump placed: Chooser 6 + trump on desk + pending 5 = 12
- After discard: Chooser 9 + trump = 10, talon 2
- After reveal: Chooser 10 in hand

## Success Criteria
- Engine supports paused dealing with trump card placement ✓
- Trump selection uses specific card (not just suit) ✓
- Trump card visibility tracked in game state ✓
- Server exposes decision endpoints with card information ✓
- UI shows trump card to all players after reveal ✓

## Related Tasks
- T-007 Engine + server basic support ✓ (Done)
- T-008 Generic chooser decision gate ✓ (Done)
- T-009 Deal pattern validation ✓ (Done)
- T-010 REST API for chooser decision ✓ (Done)
- T-011 Deal order visibility ✓ (Done)
- T-012 Web UI paused deal & trump selection ✓ (Done)
- T-013 Trump card selection and reveal ✓ (Done)
- T-035 Web UI player switcher ✓ (Done)

## Related Documentation
- T-016: Rules - Dealing and Talon (documents the rules this feature implements)
- T-018: Rules - Trump Selection and Doubling (documents trump selection rules)
