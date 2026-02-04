# T-014: Vocabulary (Czech-English Glossary)

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs

## Goal
Create a Czech-English glossary for mariáš terminology to ensure consistent
translation throughout docs/RULES.md and the codebase.

## Context
Many mariáš terms don't have standard English equivalents. This glossary
establishes the canonical translations used in this project.

## Deliverable
Create docs/VOCABULARY.md with terms organized by category.

## Initial Vocabulary

### Players & Positions
| Czech | English | Notes |
|-------|---------|-------|
| aktér | declarer | the player who won the bid |
| obrana | defense | the two players opposing declarer |
| rozdávající | dealer | deals the cards |
| volič | chooser | player after dealer, first to decide |
| zadák | cutter | player who cuts the deck |
| pauzírující | sitting out | 4th player not playing current hand |

### Game Actions
| Czech | English | Notes |
|-------|---------|-------|
| licitace | bidding/auction | the bidding phase |
| flek | double | doubles the stake |
| re | redouble | doubles again after flek |
| tutti | all double | when all players double |
| barva | suit/trump | "Barva?" = asking about trump |
| dobrá | good/accept | accepting the contract |
| pas | pass | declining to bid |

### Contracts & Game Types
| Czech | English | Notes |
|-------|---------|-------|
| hra | game (basic) | declarer needs >50 points |
| sedma | seven | win last trick with trump 7 |
| sto/stovka | hundred/kilo | declarer needs 100+ points |
| stosedma | hundred-seven | both sto and sedma |
| betl | betl | declarer must take no tricks |
| durch | durch | declarer must take all tricks |
| dvě sedmy | two sevens | both trump 7s in play |
| červená | red | hearts or diamonds as trump (2x rate) |

### Card Terms
| Czech | English | Notes |
|-------|---------|-------|
| trumf | trump | the trump suit |
| talon | talon | 2 cards set aside during deal |
| zdvih | trick | one round of card play |
| hláška | marriage | K+Q of same suit |
| pomocná | helper card | non-trump 7 in Two Sevens |

### Game Situations
| Czech | English | Notes |
|-------|---------|-------|
| ložená hra | lay-down hand | unbeatable hand, shown without play |
| tichá sedma | silent seven | unannounced seven win |
| tiché sto | silent hundred | unannounced 100+ points |
| renonc | violation/revoke | rule violation |
| omyl | mistake | folding without playing |

### Scoring
| Czech | English | Notes |
|-------|---------|-------|
| prémiové body (PB) | premium points | tournament scoring |
| paušál | penalty fee | fixed penalty for violations |
| sazba | rate/stake | payment amount |

## Definition of Done
- docs/VOCABULARY.md created with all terms
- Terms organized by category
- Notes explain non-obvious translations
- Referenced from docs/RULES.md
