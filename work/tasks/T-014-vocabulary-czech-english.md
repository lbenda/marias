# T-014: Vocabulary (Czech-English Glossary)

- Parent: F-008
- Status: Merged
- Owner: docs
- Related modules: docs

## Summary of Changes

- Created docs/VOCABULARY.md with complete Czech-English glossary
- Key terminology decisions:
  - mariáš → mariash
  - betl → misère (standard card game term)
  - durch → slam (bridge/tarot term)
  - kilo → hundred (kilo is confusing - means 1000 in SI)
  - flek/re/tutti/boty → double/redouble/raise/final raise
- Updated docs/rules/ to use English terminology throughout
- Updated F-008 feature with new vocabulary
- Updated tasks T-017, T-018, T-020, T-022, T-023 with new terminology
- Updated docs/API.md with vocabulary references
- Created T-024 for implementing vocabulary in source code

## Scope
- docs/VOCABULARY.md

## Result

- docs/VOCABULARY.md created with ~100 terms organized by category
- All documentation uses consistent English terminology
- Source code changes tracked in T-024

## Verification

- docs/VOCABULARY.md exists and is complete
- docs/rules/ references vocabulary and uses English terms
- All F-008 tasks use consistent terminology

## Goal
Create a Czech-English glossary for mariash terminology to ensure consistent
translation throughout docs/rules/ and the codebase.

## Context
Many mariash terms don't have standard English equivalents. This glossary
establishes the canonical translations used in this project.

## Deliverable
Create docs/VOCABULARY.md with terms organized by category.

## Key Vocabulary Decisions

### Game Name
| Czech | English |
|-------|---------|
| mariáš | mariash |

### Contracts
| Czech | English | Notes |
|-------|---------|-------|
| hra | game | basic contract |
| sedma | seven | win last trick with trump 7 |
| sto/stovka | hundred | 100+ points (not "kilo") |
| stosedma | hundred-seven | combined |
| betl | misère | take no tricks (tarot term) |
| durch | slam | take all tricks (bridge term) |
| dvě sedmy | two sevens | both trump 7s |

### Doubling
| Czech | English | Multiplier |
|-------|---------|------------|
| flek | double | 2x |
| re | redouble | 4x |
| tutti/výš/víc | raise | 8x |
| boty | final raise | 16x |

## Definition of Done
- docs/VOCABULARY.md created with all terms ✓
- Terms organized by category ✓
- Notes explain non-obvious translations ✓
- Referenced from docs/rules/ ✓
- All tasks/features updated with vocabulary ✓
- T-024 created for source code changes ✓
