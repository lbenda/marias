# T-052: Contract metadata in universal state model

- Type: Task
- Status: Todo
- Feature: F-016
- Source: R-006
- Architecture: A-013 (Rule-Based Engine and Action Provider)
- Depends on: T-051

## Goal

Design and implement immutable metadata structures for the Contract Selection phase that integrate with A-013's universal state model, enabling contract commitments, doubling levels, and Open Hand state to be tracked alongside the universal card map.

## Scope

**IN:**
- `ContractSelectionMetadata` data class definition
- `Commitment` data class (contract type, owner, doubling level)
- `OpenHandState` data class (declaring player, defense responses)
- Immutable transformation helper functions (withDeclarerCommitment, withDefenseCommitment, etc.)
- Query extension functions on `GameState` (getDeclarerCommitment, canPlayerDouble, etc.)
- Doubling level to multiplier mapping (0-4 → 1x-16x)
- JSON serialization support with Kotlin serialization
- Integration with polymorphic `GameState.metadata` field

**OUT:**
- RuleSet logic (covered in T-051)
- Server API (covered in T-053)
- UI components (covered in T-054)
- Actual reduction logic (in T-051's `RuleSet.reduce()`)

## Objective

Implement the polymorphic metadata structures for Contract Selection phase following A-013's universal state model. This metadata lives within `GameState` alongside the universal card map and is managed through the `RuleSet.reduce()` function.

## Requirements

### 1. Commitment Data Structure

Define commitment representation:
```
Commitment:
  - id: unique identifier
  - type: CONTRACT (Game/Seven/Hundred/Hundred-Seven)
  - owner: DECLARER | DEFENSE_PLAYER_1 | DEFENSE_PLAYER_2
  - doublingLevel: 0-4 (maps to 1x, 2x, 4x, 8x, 16x)
  - lastDoubledBy: DECLARER | DEFENSE
  - fulfilled: null (until scoring phase)
```

### 2. Contract Selection State

Define phase state:
```
ContractSelectionState:
  - phase: CONTRACT_SELECTION
  - declarerCommitment: Commitment (required)
  - defenseCommitments: List<Commitment> (0-2 entries)
  - currentRespondingPlayer: PlayerId | null
  - playersResponded: Set<PlayerId>
  - openHandState: null | OpenHandDeclaration
  - phaseComplete: boolean
```

### 3. Open Hand State

Define Open Hand tracking:
```
OpenHandDeclaration:
  - declaringPlayer: PlayerId
  - cardsRevealed: List<Card>
  - defenseResponses: Map<PlayerId, ACCEPT | REJECT>
  - resolved: boolean
  - accepted: boolean (true if both defense players accept)
```

### 4. Doubling Level Mapping

Map doubling levels to multipliers:
- Level 0: 1x (no doubling)
- Level 1: 2x (double)
- Level 2: 4x (redouble)
- Level 3: 8x (raise)
- Level 4: 16x (reraise)

### 5. Immutable State Transformations

**Note:** Per A-013, state is **immutable**. All updates happen through `RuleSet.reduce()` returning new `GameState` instances. Do NOT implement mutable setters.

Implement helper functions for creating new metadata instances:
- `withDeclarerCommitment(old: Metadata, commitment: Commitment): Metadata`
- `withDefenseCommitment(old: Metadata, commitment: Commitment): Metadata`
- `withIncrementedDoubling(old: Metadata, commitmentId: String, side: Side): Metadata`
- `withPlayerResponded(old: Metadata, playerId: PlayerId): Metadata`
- `withOpenHandState(old: Metadata, openHandState: OpenHandState): Metadata`
- `withPhaseComplete(old: Metadata): Metadata`

### 6. Query Extensions

Implement extension functions on `GameState` for querying contract metadata:
- `GameState.getContractMetadata(): ContractSelectionMetadata?`
- `GameState.getDeclarerCommitment(): Commitment?`
- `GameState.getDefenseCommitments(): List<Commitment>`
- `GameState.getCommitmentMultiplier(commitmentId: String): Int`
- `GameState.canPlayerDouble(playerId: PlayerId, commitmentId: String): Boolean`
- `GameState.getAvailableContractsForDefense(playerId: PlayerId): List<ContractType>`
- `GameState.isContractPhaseComplete(): Boolean`

**Note:** These are pure read-only queries. They do NOT mutate state.

### 7. JSON Serialization

Implement JSON serialization for contract metadata using Kotlin serialization:
- `@Serializable` annotations on all metadata classes
- Custom serializers if needed for complex types
- Ensure metadata can be included in `GameState` JSON representation
- Support deserialization from JSON to restore game state

### 8. Integration with GameState

Ensure metadata integrates cleanly with A-013 universal state model:
- Metadata stored in `GameState.metadata` field (polymorphic)
- Type discrimination for different phase metadata
- Coexists with universal card map (`Map<Card, CardPosition>`)
- Does not duplicate card information (cards tracked only in card map)

## Acceptance Criteria

- [ ] `Commitment` data class defined with all required fields
- [ ] `ContractSelectionMetadata` data class defined and extends base metadata
- [ ] `OpenHandState` data class defined
- [ ] Doubling level to multiplier mapping functions correctly (0-4 → 1x-16x)
- [ ] All immutable transformation helper functions implemented
- [ ] All query extension functions on `GameState` implemented
- [ ] Metadata is immutable (all fields `val`, copy() for updates)
- [ ] JSON serialization/deserialization works correctly
- [ ] Unit tests cover all transformation functions
- [ ] Unit tests cover all query functions
- [ ] Integration with `GameState` verified

## Definition of Done

- [ ] `Commitment` data class defined with all fields (id, contractType, owner, doublingLevel, lastDoubledBy)
- [ ] `ContractSelectionMetadata` data class defined extending base metadata interface
- [ ] `OpenHandState` data class defined with declaring player and defense responses
- [ ] All metadata classes are immutable (all fields `val`, no mutable collections)
- [ ] Immutable transformation helpers implemented (withDeclarerCommitment, withDefenseCommitment, withIncrementedDoubling, etc.)
- [ ] Query extension functions on `GameState` implemented and tested
- [ ] Doubling level to multiplier mapping function works correctly (0→1x, 1→2x, 2→4x, 3→8x, 4→16x)
- [ ] JSON serialization/deserialization works with `@Serializable` annotations
- [ ] Polymorphic metadata integration with `GameState.metadata` field verified
- [ ] Metadata coexists properly with universal card map (no data duplication)
- [ ] Unit tests cover all transformation helper functions
- [ ] Unit tests cover all query extension functions
- [ ] Unit tests verify immutability (calling copy() creates new instance)
- [ ] Unit tests verify JSON serialization round-trip

## Related

- F-016: Contract Selection and Doubling
- R-006: Contract (Commitments)
- A-013: Rule-Based Engine and Action Provider
- T-051: RuleSet extension for contract selection (prerequisite)
- T-053: Server API for contract selection (next)
