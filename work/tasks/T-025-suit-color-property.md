# T-025: Add Suit Color Property

- Parent: F-009
- Status: Planned
- Owner: engine
- Related modules: engine
- Depends on: none

## Goal
Add a `color` property to the `Suit` enum to distinguish red suits (Hearts, Diamonds)
from black suits (Spades, Clubs).

## Background
From docs/RULES.md:
> **Red suits** (Hearts, Diamonds): When chosen as trump, all payment rates are doubled.

The `Suit` enum currently only has a `symbol` property. A `color` property is needed
for the red trump doubling rule.

## Scope

### Add SuitColor enum
```kotlin
enum class SuitColor { BLACK, RED }
```

### Update Suit enum
```kotlin
enum class Suit(val symbol: String, val color: SuitColor) {
    SPADES("♠", SuitColor.BLACK),
    CLUBS("♣", SuitColor.BLACK),
    DIAMONDS("♦", SuitColor.RED),
    HEARTS("♥", SuitColor.RED);

    val isRed: Boolean get() = color == SuitColor.RED
}
```

## Files to Modify
- `engine/src/main/kotlin/cz/lbenda/games/marias/engine/model/Suit.kt`

## Definition of Done
- Suit has color property
- Suit has isRed helper property
- All existing tests pass
- Unit tests for color property
