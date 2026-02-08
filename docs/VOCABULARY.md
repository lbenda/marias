# Mariash Vocabulary (Czech-English)

This glossary defines the canonical English translations for Czech mariash terminology
used throughout this project's documentation and codebase.

Source: [Czech Mariash Association Rules](https://www.talon.cz/pravidla/mariáš_pravidla_licitovaný_2014.pdf)

---

## Game Name

| Czech | English | Notes |
|-------|---------|-------|
| mariáš | mariash | Czech trick-taking card game |

## Players & Positions

| Czech         | English     | Code          | Notes                                                  |
|---------------|-------------|---------------|--------------------------------------------------------|
| aktér         | declarer    | `declarerId`  | Player who won the bid and plays against defense       |
| obrana        | defense     | -             | The two players opposing the declarer                  |
| rozdávající   | dealer      | `dealerIndex` | Deals the cards, rotates each round                    |
| forhont/volič | chooser     | `chooserId`   | Player after dealer; first to decide on trump          |
| zadák         | cutter      | -             | Player who cuts the deck (before dealer)               |
| pauzírující   | sitting out | -             | 4th player not playing current hand (4-player variant) |
| spoluhráč     | partner     | -             | Teammate (in defense)                                  |

## Game Phases

| Czech | English | Code | Notes |
|-------|---------|------|-------|
| rozdávání | dealing | `DEALING` | Cards being distributed |
| licitace | bidding | `BIDDING` | Auction phase for contracts |
| výměna talonu | talon exchange | `TALON_EXCHANGE` | Declarer exchanges cards |
| volba trumfu | trump selection | `TRUMP_SELECTION` | Declaring trump suit |
| sehrávka | play/tricks | `PLAYING` | Trick-taking phase |
| počítání | scoring | `SCORING` | Counting points |

## Doubling (Flekování)

Similar to bridge doubling, but with 4 levels:

| Czech           | English  | Code       | Multiplier | Notes                     |
|-----------------|----------|------------|------------|---------------------------|
| flek            | double   | `DOUBLE`   | 2x         | First level, like bridge  |
| re              | redouble | `REDOUBLE` | 4x         | Second level, like bridge |
| tutti, výš, víc | raise    | `RAISE`    | 8x         | Third level (higher/more) |
| boty (retuti)   | reraise  | `RERAISE`  | 16x        | Fourth level              |
| kalhoty         | surge    | `SURGE`    | 32x        | Fifth level               |
| kaiser          | resurge  | `RESURGE`  | 64x        | Sixth level               |

- Doubling follows clockwise order strictly
- Must be expressed clearly with words
- Maximum 6 levels (beyond is invalid but not penalized)

## Other Game Actions

| Czech | English | Code | Notes |
|-------|---------|------|-------|
| dobrá | good/accept | - | Accepting the announced contract |
| pas | pass | `Pass` | Declining to bid or act |
| barva | suit/trump | - | "Barva?" = asking if trump accepted |
| hrát | play | `PlayCard` | Playing a card |
| vynést | lead | - | Play first card of trick |

## Contracts & Game Types

| Czech               | English       | Code            | Notes                                                  |
|---------------------|---------------|-----------------|--------------------------------------------------------|
| hra / barva         | trump game    | `TRUMP_GAME`    | Basic contract: declarer needs >50 points              |
| sedma               | seven         | `SEDMA`         | Win last trick with trump 7                            |
| sto, stovka, (kilo) | hundred       | `HUNDRED`       | Declarer needs ≥100 points                             |
| stosedma            | hundred-seven | `HUNDRED_SEVEN` | Both Hundred and Seven combined                        |
| betl                | misère        | `MISERE`        | Declarer must take NO tricks (standard card game term) |
| durch               | slam          | `SLAM`          | Declarer must take ALL tricks (bridge/tarot term)      |
| dvě sedmy           | two sevens    | `TWO_SEVENS`    | Both trump 7s must be controlled                       |

### Contract Modifiers

| Czech | English | Notes |
|-------|---------|-------|
| červená | red | Hearts or diamonds as trump; doubles rate |
| tichá sedma | silent seven | Unannounced Seven win (half value) |
| tiché sto | silent hundred | Unannounced 100+ points (doubles game value) |
| omyl | mistake/fold | Bidding Seven then folding without play |

## Card Terms

| Czech | English | Code | Notes |
|-------|---------|------|-------|
| karta | card | `Card` | Single playing card |
| barva | suit | `Suit` | Spades, Clubs, Diamonds, Hearts |
| hodnota | rank | `Rank` | 7, 8, 9, 10, J, Q, K, A |
| trumf | trump | `trump` | The trump suit for current game |
| trumfová karta | trump card | `trumpCard` | Specific card used to declare trump |
| talon | talon | `talon` | 2 cards set aside; declarer exchanges |
| zdvih | trick | `trick` | One round of card play (3 cards) |
| ruka | hand | `hand` | Cards held by a player |
| balíček | deck | `deck` | Full set of 32 cards |

### Card Ranks

| Czech | English | Code | Points |
|-------|---------|------|--------|
| sedmička, sedma | seven | `SEVEN` | 0 |
| osmička | eight | `EIGHT` | 0 |
| devítka | nine | `NINE` | 0 |
| desítka | ten | `TEN` | 10 |
| spodek, kluk | jack | `JACK` | 2 |
| svršek, dáma | queen | `QUEEN` | 3 |
| král | king | `KING` | 4 |
| eso | ace | `ACE` | 11 |

### Card Suits

| Czech | English | Code | Color |
|-------|---------|------|-------|
| zelené, listy | spades | `SPADES` | black |
| žaludy | clubs | `CLUBS` | black |
| kule | diamonds | `DIAMONDS` | red |
| srdce | hearts | `HEARTS` | red |

## Special Terms

| Czech | English        | Notes |
|-------|----------------|-------|
| hláška | marriage       | King + Queen of same suit; bonus points |
| pomocná | helper card    | Non-trump 7 in Two Sevens contract |
| ložená hra | open hand      | Unbeatable hand; must show, not play |
| vynesení | lead           | First card played in a trick |
| přebití | overtrump      | Playing higher trump than previous |
| přiznání barvy | following suit | Playing same suit as led |

## Violations & Penalties

| Czech | English | Notes |
|-------|---------|-------|
| renonc | violation/revoke | Rule violation during play |
| paušál | penalty fee | Fixed penalty amount |
| paušál 1 | minor penalty | Standard violation fee |
| paušál 2 | major penalty | Severe violation fee (5x paušál 1) |
| technický renonc | technical violation | Procedural violation |

### Common Violations

| Czech | English |
|-------|---------|
| nepřiznání barvy | failure to follow suit |
| nepřebití | failure to beat |
| nesprávné vynesení | incorrect lead |
| předhození karty | playing out of turn |
| zhlédnutí cizích karet | looking at others' cards |

## Scoring Terms

| Czech | English | Notes |
|-------|---------|-------|
| body | points | Card point values |
| prémiové body (PB) | premium points | Tournament scoring |
| sazba | rate/stake | Payment amount |
| základ | base | Base rate for calculations |
| limit | limit | Maximum payment (100x base) |
| zvýšený limit | raised limit | Higher maximum (150x base) |

## Tournament Terms

| Czech | English | Notes |
|-------|---------|-------|
| soutěžní mariáš | tournament mariash | Competitive play with official rules |
| licitovaný mariáš | auction mariash | Standard competitive variant |
| stolový lístek | score sheet | Table-level scoring record |
| rozhodčí | referee | Tournament official |
| hrací kolo | playing round | One rotation of dealing |

---

*This vocabulary is used consistently in docs/rules/*.md and the codebase.*
