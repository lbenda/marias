# F-008: Complete Rules Documentation

* Type: Feature
* Status: In Progress
* Source: https://www.talon.cz/pravidla/mariáš_pravidla_licitovaný_2014.pdf (Czech Mariash Association, 2014)

## Description
Translate and document the complete official mariash rules from Czech to English
in docs/RULES.md. The source document contains tournament rules for "auction mariash"
which is the standard competitive variant.

A Czech-English vocabulary/glossary (docs/VOCABULARY.md) ensures consistent terminology
throughout the documentation and codebase.

## Structure (from source document)

The rules are organized into 5 articles:

1. **Article I - Bidding Ladder & Rates**
   - 12 contract levels from Seven to Two Sevens red trump and Hundred
   - Payment rates and multipliers
   - Red trump doubles the rate

2. **Article II - Game Rules**
   - 25 rules covering:
   - Dealing and cutting
   - Talon handling
   - Trump selection and bidding
   - Doubling system (double/redouble/raise/final raise)
   - Lay-down hands
   - Time limits

3. **Article III - Two Sevens Special Rules**
   - Showing helper cards (4+ helpers)
   - Special cases for Two Sevens and Hundred

4. **Article IV - Violations**
   - Minor penalty violations
   - Major penalty violations
   - Technical violations
   - Consequences and penalties

5. **Article V - Miscellaneous**
   - Violation claims
   - Referee decisions

## Success Criteria
- Czech-English vocabulary with consistent terminology
- Complete English translation of all rules in docs/RULES.md
- Rules are clear enough for someone unfamiliar with mariash to learn the game
- Implementation features/tasks identified for each rule section
- Source code updated to use vocabulary

## Related Tasks
- T-014: Vocabulary (Czech-English glossary)
- T-015: Equipment and card values
- T-016: Dealing and talon rules
- T-017: Bidding ladder and contract types
- T-018: Trump selection and doubling
- T-019: Trick-taking rules
- T-020: Scoring and payment
- T-021: Marriages
- T-022: Violations and penalties
- T-023: Two Sevens special rules
- T-024: Implement vocabulary in source code
