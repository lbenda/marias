# F-008: Complete Rules Documentation

* Type: Feature
* Status: In Progress
* Source: https://www.talon.cz/pravidla/mariáš_pravidla_licitovaný_2014.pdf (Czech Mariáš Association, 2014)

## Description
Translate and document the complete official mariáš rules from Czech to English
in docs/RULES.md. The source document contains tournament rules for "licitovaný mariáš"
(auction/bidding mariáš) which is the standard competitive variant.

A Czech-English vocabulary/glossary will be created to ensure consistent terminology
throughout the documentation and codebase.

## Structure (from source document)

The rules are organized into 5 articles:

1. **Article I - Bidding Ladder & Rates** (Licitační žebříček)
   - 12 contract levels from Sedma to Dvě sedmy červená trumf a sto
   - Payment rates and multipliers
   - Red trump (červená) doubles the rate

2. **Article II - Game Rules** (Herní ustanovení)
   - 25 rules covering:
   - Dealing and cutting
   - Talon handling
   - Trump selection and bidding
   - Flekování (doubling/redoubling)
   - "Ložená hra" (lay-down hands)
   - Time limits

3. **Article III - Two Sevens Special Rules** (Dvě sedmy)
   - Showing helper cards (4+ helpers)
   - Special cases for Dvě sedmy a Sto

4. **Article IV - Violations (Renonc)**
   - Paušál 1 violations (minor)
   - Paušál 2 violations (major)
   - Technical violations
   - Consequences and penalties

5. **Article V - Miscellaneous**
   - Violation claims
   - Referee decisions

## Success Criteria
- Czech-English vocabulary with consistent terminology
- Complete English translation of all rules in docs/RULES.md
- Rules are clear enough for someone unfamiliar with mariáš to learn the game
- Implementation features/tasks identified for each rule section

## Related Tasks
- T-014: Vocabulary (Czech-English glossary)
- T-015: Equipment and card values
- T-016: Dealing and talon rules
- T-017: Bidding ladder and contract types
- T-018: Trump selection and flekování
- T-019: Trick-taking rules
- T-020: Scoring and payment
- T-021: Marriages (hlášky)
- T-022: Violations and penalties
- T-023: Two Sevens special rules
