# T-021: Rules - Marriages (Hlášky)

- Parent: F-008
- Status: Planned
- Owner: docs
- Related modules: docs
- Depends on: T-014

## Goal
Document marriage (hláška) rules in docs/RULES.md.

## Scope

### What is a Marriage
- King and Queen of the same suit in hand
- Announced when playing one of the pair
- Adds bonus points

### Marriage Values
| Marriage Type | Points |
|---------------|--------|
| Non-trump marriage | 20 |
| Trump marriage | 40 |

### Announcement Rules
- Can only announce when leading a trick
- Must hold both K and Q at announcement time
- Play K or Q to announce
- Cannot announce "sedma ani sto proti" (rule 23)

### Restrictions
- Cannot announce Seven or Hundred "against" (counter-announcement)
- Marriage cards can be part of talon discard considerations

## Implementation Notes
- Marriage tracking already has TODO in GameReducer
- Need to implement marriage announcement action
- Need to track announced marriages for scoring

## Definition of Done
- Marriage rules complete in docs/RULES.md
- Values and timing documented
- Restrictions documented
