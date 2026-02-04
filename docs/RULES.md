
# Mariáš - Game Rules

Mariáš is a traditional Czech trick-taking card game for 3 players.
This document describes the rules as implemented in this game engine.

## Equipment

### Deck
32 cards (German or French suits):
- 4 suits: Spades, Clubs, Diamonds, Hearts
- 8 ranks per suit: 7, 8, 9, 10, Jack, Queen, King, Ace

### Card Values (Points)

| Rank  | Points | Strength |
|-------|--------|----------|
| Seven | 0      | 1 (lowest) |
| Eight | 0      | 2 |
| Nine  | 0      | 3 |
| Ten   | 10     | 4 |
| Jack  | 2      | 5 |
| Queen | 3      | 6 |
| King  | 4      | 7 |
| Ace   | 11     | 8 (highest) |

**Total deck points: 120**

## Players and Seating

- 3 players sit around the table
- Positions rotate each round:
  - **Dealer** (rozdávající) - deals cards
  - **Chooser** (volič) - sits after dealer, makes first decisions
  - **Third player** - sits after chooser

## Dealing (Rozdávání)

Dealing happens in two phases:

### Phase A - Initial Deal
1. Dealer shuffles and deals cards
2. **Chooser receives 7 cards first**
3. Other players receive their full 10 cards
4. 2 cards remain face-down on table (talon)

### Chooser's Decision
After receiving 7 cards, the chooser must decide:

1. **Select trump** - Pick one card from hand and place it **face-down** on the table
   - This card determines the trump suit
   - The card is NOT revealed yet
   - Continue to Phase B

2. **Pass** (Pas) - Decline to choose trump
   - Proceed to bidding phase
   - Other players may bid for contracts

### Phase B - Completing the Deal
If chooser selected trump:
1. Chooser receives remaining **5 cards** (now has 12 cards total)
2. Chooser discards **2 cards** face-down to the talon
3. Chooser now has 10 cards in hand

### Talon Exchange (Výměna talonu)
After discarding, the chooser asks: **"Barva?"** (Trump?)

Other players respond in order:
- **"Dobrá"** (Good) - Accept the trump, game proceeds normally
- **Take talon** - Player takes the 2 talon cards and declares Betl or Durch

### Trump Reveal (Vyložení trumfu)
If all players say "Dobrá":
1. Chooser **flips the trump card** face-up on the desk
2. Chooser **announces the game type** with trump suit:
   - **"Hra v srdcích"** - Basic game in Hearts
   - **"Sedma v srdcích"** - Sedma in Hearts (win last trick with trump 7)
   - **"Stovka v srdcích"** - Kilo in Hearts (score 100+ points)
   - **"Stosedma v srdcích"** - Both Sedma and Kilo combined
3. Chooser **takes the trump card back** into their hand
4. Chooser leads the first trick

**Card count at game start:**
- Each player: 10 cards
- Talon (discarded): 2 cards
- Total: 32 cards

## Game Types (Druhy her)

| Type | Czech | Description |
|------|-------|-------------|
| HRA | Hra | Basic game - declarer needs more than 50 points |
| SEDMA | Sedma | Win last trick with 7 of trumps |
| KILO | Kilo/Stovka | Declarer needs 100+ points |
| STOSEDMA | Stosedma | Sedma + Kilo combined |
| BETL | Betl | Declarer must not win any trick |
| DURCH | Durch | Declarer must win all tricks |

**Note:** The revealed trump card is visible to all players - they know both the trump suit AND which specific card was used to declare it. This information is strategically important.

## Playing Tricks

*(To be documented)*

## Scoring

*(To be documented)*

## Marriages (Hlášky)

*(To be documented)*

---

*This document is incrementally updated as rules are clarified.*
