# Mariash - Game Rules

Mariash is a traditional Czech trick-taking card game for 3 players.
This document describes the rules as implemented in this game engine.

For Czech-English terminology, see [VOCABULARY.md](VOCABULARY.md).

## Equipment

### Deck
32 cards using either German or French suits:

| French | German | Color |
|--------|--------|-------|
| Spades | Leaves (Zelené) | Black |
| Clubs | Acorns (Žaludy) | Black |
| Diamonds | Bells (Kule) | Red |
| Hearts | Hearts (Srdce) | Red |

Each suit has 8 ranks: 7, 8, 9, 10, Jack, Queen, King, Ace

**Red suits** (Hearts, Diamonds): When chosen as trump, all payment rates are doubled.

### Card Values

| Rank  | Points | Strength | Notes |
|-------|--------|----------|-------|
| Seven | 0      | 1 (lowest) | Special role in Seven contract |
| Eight | 0      | 2 | |
| Nine  | 0      | 3 | |
| Ten   | 10     | 4 | High points, mid strength |
| Jack  | 2      | 5 | |
| Queen | 3      | 6 | Marriage with King |
| King  | 4      | 7 | Marriage with Queen |
| Ace   | 11     | 8 (highest) | |

**Total deck points: 120** (30 points per suit)

**Note:** The Ten is valuable (10 points) but can be beaten by Jack, Queen, King, or Ace. Protecting your Tens is a key tactical consideration.

## Players and Seating

- 3 players sit around the table
- Positions rotate each round:
  - **Dealer** - deals cards
  - **Chooser** - sits after dealer, makes first decisions
  - **Third player** - sits after chooser

## Dealing

### Cutting (Not Shuffling)
In tournament play, cards are **cut, not shuffled**:
- Cutter (player before dealer) cuts with one hand only
- Cut at least 2 cards from top or bottom
- Cannot lift or raise the whole deck
- No counting, sliding, or leafing through cards
- Cutter may request dealer to reshape the deck

**Note:** Casual games may use shuffling. The engine supports both modes.

### Dealing Direction
Cards are dealt **clockwise**, starting with the chooser.

### Dealing Mistakes
One free dealing mistake is allowed per round. Second mistake in the same round is a violation.

### Two-Phase Deal

#### Phase A - Initial Deal
1. Dealer cuts (or shuffles in casual mode) and deals cards
2. **Chooser receives 7 cards first**
3. Other players receive their full 10 cards
4. 2 cards remain face-down on table (talon)

### Chooser's Decision
After receiving 7 cards, the chooser must decide:

1. **Select trump** - Pick one card from hand and place it **face-down** on the table
   - This card determines the trump suit
   - The card is NOT revealed yet
   - Continue to Phase B

2. **Pass** - Decline to choose trump
   - Proceed to bidding phase
   - Other players may bid for contracts

### Phase B - Completing the Deal
If chooser selected trump:
1. Chooser receives remaining **5 cards** (now has 12 cards total)
2. Chooser discards **2 cards** face-down to the talon
3. Chooser now has 10 cards in hand

### Talon Exchange
After discarding, the chooser asks: **"Trump?"**

Other players respond in order:
- **"Good"** - Accept the trump, game proceeds normally
- **Take talon** - Player takes the 2 talon cards and declares Misère or Slam

### Trump Reveal
If all players say "Good":
1. Chooser **flips the trump card** face-up on the desk
2. Chooser **announces the game type** with trump suit:
   - **"Game in Hearts"** - Basic game in Hearts
   - **"Seven in Hearts"** - Seven in Hearts (win last trick with trump 7)
   - **"Hundred in Hearts"** - Hundred in Hearts (score 100+ points)
   - **"Hundred-Seven in Hearts"** - Both Seven and Hundred combined
3. Chooser **takes the trump card back** into their hand
4. Chooser leads the first trick

**Card count at game start:**
- Each player: 10 cards
- Talon (discarded): 2 cards
- Total: 32 cards

### Talon Rules

**Ownership:**
- The talon belongs to no player and must remain separate
- Talon points count toward the **declarer's** score at end of round

**Discard Restrictions:**
- **Aces and Tens cannot be discarded** to the talon (major violation)
- Exception: Misère and Slam contracts allow any cards to be discarded
- Announced Seven cannot be discarded to talon

**Timing:**
- Talon can only be changed before the first defender comments on trump
- After play starts, no one may look at the talon
- For Misère/Slam: talon must never be looked at

**Wrong Card Count:**
- If discovered before play: can be doubled, then warned (violation)
- If discovered after play starts: game violation

### Card Handling After Play

**Trick Management:**
- Each player keeps their own won tricks in a pile
- Trick order must not be disturbed (for verification)

**Round Cleanup:**
- Unplayed cards must be sorted by suit and placed face-down
- Dealer collects cards in specific order for next round

## Game Types

| Type | Description |
|------|-------------|
| Game | Basic game - declarer needs more than 50 points |
| Seven | Win last trick with 7 of trumps |
| Hundred | Declarer needs 100+ points |
| Hundred-Seven | Seven + Hundred combined |
| Misère | Declarer must not win any trick |
| Slam | Declarer must win all tricks |

**Note:** The revealed trump card is visible to all players - they know both the trump suit AND which specific card was used to declare it. This information is strategically important.

## Playing Tricks

*(To be documented)*

## Scoring

*(To be documented)*

## Marriages

*(To be documented)*

---

*This document is incrementally updated as rules are clarified.*
