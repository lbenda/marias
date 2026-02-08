# T-051: RuleSet extension for contract selection

- Type: Task
- Status: Todo
- Feature: F-016
- Source: R-006
- Architecture: A-013 (Rule-Based Engine and Action Provider)

## Goal

Extend the Mariash `RuleSet` to support the Contract Selection phase (R-006), enabling the declarer to announce contracts, defense to respond with doubling or counter-contracts, and automatic phase transitions in/out of this phase following the Action Provider pattern.

## Scope

**IN:**
- Automatic phase transition FROM Game Selection (R-005) when Trump Game selected
- Contract Selection phase metadata (`ContractSelectionMetadata`)
- GameAction extensions for contract actions (AnnounceContract, DoubleCommitment, etc.)
- `RuleSet.possibleActions()` generation for contracts, doubling, and defense responses
- `RuleSet.reduce()` implementation for all contract-related actions
- `RuleSet.validate()` enforcement of R-006 rules (turn order, doubling limits, duplicate prevention)
- Doubling mechanics (4 levels: 2x, 4x, 8x, 16x with alternating raises)
- Defense counter-commitments (independent from declarer's commitment)
- Open Hand declaration and response flow
- Card visibility updates (trump card reveal, Open Hand cards visible)
- Automatic phase transition TO Playing Tricks (R-007) with trump card return
- Special case: Open Hand acceptance skips to SCORING phase

**OUT:**
- Server API implementation (covered in T-053)
- UI implementation (covered in T-054)
- Scoring calculations (will be in R-009 implementation)
- Playing Tricks phase implementation (separate task for R-007)

## Objective

Extend the `RuleSet` for Mariash to support the Contract Selection phase using the Action Provider pattern defined in A-013. The `RuleSet` will:
1. **Transition INTO** Contract Selection when Trump Game is selected (from R-005 Game Selection phase)
2. Generate `possibleActions` for contract announcements, doubling, and defense responses
3. Reduce contract-related actions into state updates
4. **Transition OUT** to Playing Tricks (R-007) after all responses complete

## Requirements

### 0. Phase Transition: Entry from Game Selection

**Trigger:** When Game Selection phase (R-005) completes with Trump Game selected

Implement automatic phase transition in `RuleSet.reduce()`:

```kotlin
// After SelectGameType(TRUMP_GAME) action is reduced and all players accept:
when (finalGameType) {
  GameType.TRUMP_GAME -> {
    // Automatic transition to CONTRACT_SELECTION
    val newMetadata = ContractSelectionMetadata(
      phase = Phase.CONTRACT_SELECTION,
      declarerCommitment = null,
      defenseCommitments = emptyList(),
      currentRespondingPlayerId = declarerId, // declarer announces first
      playersResponded = emptySet(),
      openHandState = null,
      phaseComplete = false
    )
    state.copy(metadata = newMetadata)
  }
  GameType.MISERE, GameType.SLAM -> {
    // Skip CONTRACT_SELECTION, go directly to BIDDING phase
    // (handled by different task for bidding)
  }
}
```

**Key points:**
- This transition is **automatic** (no player action required)
- Trump card remains revealed on table from Game Selection phase
- Declarer is set from Game Selection phase
- Defense players determined by position (clockwise from declarer)

### 1. Metadata Extension

Extend the polymorphic `metadata` object in `GameState` to include:

```kotlin
ContractSelectionMetadata {
  phase: Phase.CONTRACT_SELECTION
  declarerCommitment: Commitment?
  defenseCommitments: List<Commitment> // max 2
  currentRespondingPlayerId: PlayerId?
  playersResponded: Set<PlayerId>
  openHandState: OpenHandState?
  phaseComplete: Boolean
}

Commitment {
  id: String
  contractType: ContractType // GAME, SEVEN, HUNDRED, HUNDRED_SEVEN
  owner: CommitmentOwner // DECLARER, DEFENSE_PLAYER_1, DEFENSE_PLAYER_2
  doublingLevel: Int // 0-4 (maps to 1x, 2x, 4x, 8x, 16x)
  lastDoubledBy: Side // DECLARER, DEFENSE
}

OpenHandState {
  declaringPlayerId: PlayerId
  defenseResponses: Map<PlayerId, Boolean> // true=accept, false=reject
  resolved: Boolean
}
```

### 2. GameAction Extensions

Add sealed class variants to `GameAction`:

```kotlin
sealed class GameAction {
  // ... existing actions ...

  // Contract Selection Actions
  data class AnnounceContract(val playerId: PlayerId, val contract: ContractType) : GameAction()
  data class AcceptContracts(val playerId: PlayerId) : GameAction()
  data class DoubleCommitment(val playerId: PlayerId, val commitmentId: String) : GameAction()
  data class AnnounceDefenseContract(val playerId: PlayerId, val contract: ContractType) : GameAction()
  data class DeclareOpenHand(val playerId: PlayerId) : GameAction()
  data class RespondToOpenHand(val playerId: PlayerId, val accept: Boolean) : GameAction()
}
```

### 3. RuleSet.possibleActions() Extension

Extend `possibleActions(state: GameState, playerId: PlayerId): List<GameAction>` to generate contract selection actions:

**For declarer (when phase just started):**
- `AnnounceContract(playerId, GAME)`
- `AnnounceContract(playerId, SEVEN)`
- `AnnounceContract(playerId, HUNDRED)`
- `AnnounceContract(playerId, HUNDRED_SEVEN)`
- `DeclareOpenHand(playerId)` (optional)

**For defense players (in turn):**
- `AcceptContracts(playerId)` (always available)
- `DoubleCommitment(playerId, commitmentId)` for each commitment (if doubling rules allow)
- `AnnounceDefenseContract(playerId, contract)` for each available contract (excluding duplicates)

**For defense players (responding to Open Hand):**
- `RespondToOpenHand(playerId, accept=true)`
- `RespondToOpenHand(playerId, accept=false)`

### 4. RuleSet.reduce() Extension

Extend `reduce(state: GameState, action: GameAction): GameState` to handle contract actions:

**AnnounceContract:**
- Create `Commitment` for declarer
- Update metadata with commitment
- Set next responding player (first defense player clockwise)
- Update card visibility if trump card needs to be revealed
- Return updated state

**AcceptContracts:**
- Mark player as responded
- Advance to next player or complete phase
- If phase complete and no Open Hand: trigger automatic action to transition to PLAYING_TRICKS
- Return updated state

**DoubleCommitment:**
- Increment doubling level on specified commitment
- Update `lastDoubledBy`
- Mark player as responded
- Advance to next player
- Return updated state

**AnnounceDefenseContract:**
- Create `Commitment` for defense player
- Add to `defenseCommitments`
- Mark player as responded
- Advance to next player
- Return updated state

**DeclareOpenHand:**
- Create `OpenHandState`
- Update card visibility (declarer's cards visible to all)
- Set next responding players (both defense)
- Return updated state

**RespondToOpenHand:**
- Record defense response
- If both defense players responded:
  - If both accept: trigger auto-win (skip to SCORING phase)
  - If any reject: proceed to PLAYING_TRICKS with cards visible
- Return updated state

### 5. RuleSet.validate() Extension

Extend `validate(state: GameState, action: GameAction): ValidationResult` to enforce:

- Only declarer can announce initial contract
- Only current responding player can act
- Defense cannot announce duplicate contracts
- Doubling follows alternating raise rules
- Maximum 4 doubling levels
- Open Hand can only be declared before contracts are announced
- Phase must be CONTRACT_SELECTION

Return `ValidationResult.Invalid(reason)` with clear error messages for violations.

### 6. Automatic Action Chaining & Phase Transition: Exit to Playing Tricks

**Trigger:** When all players have responded and no one raises further

Implement automatic phase transition when phase completes:

```kotlin
// In RuleSet.reduce() after final player accepts/responds:
if (allPlayersResponded && noRaisesRemaining) {
  // Generate automatic actions in sequence:

  // 1. Return trump card to declarer's hand
  val trumpCard = state.cardMap.entries.find { it.value.path == "table/trump" }?.key
  val updatedCardMap = state.cardMap + (trumpCard to CardPosition(
    path = "player[${declarerId}]/hand[${nextIndex}]",
    visibility = listOf(declarerId) // only declarer sees it
  ))

  // 2. Transition to PLAYING_TRICKS phase
  val playingTricksMetadata = PlayingTricksMetadata(
    phase = Phase.PLAYING_TRICKS,
    currentPlayerTurn = declarerId, // declarer leads first trick
    currentTrick = emptyList(),
    tricksWon = mapOf(),
    commitments = allCommitments, // carry over from contract selection
    openHand = openHandState?.resolved == true && !accepted // cards stay visible if open hand rejected
  )

  return state.copy(
    cardMap = updatedCardMap,
    metadata = playingTricksMetadata
  )
}
```

**Special case: Open Hand Auto-Win**

If Open Hand is accepted by both defense players:

```kotlin
// Skip PLAYING_TRICKS entirely, go to SCORING
if (openHandState?.resolved == true && bothDefenseAccepted) {
  val scoringMetadata = ScoringMetadata(
    phase = Phase.SCORING,
    declarerAutoWin = true,
    allContractsFulfilled = true,
    // ... scoring details
  )
  return state.copy(metadata = scoringMetadata)
}
```

**Key points:**
- Phase transition is **automatic** (part of action chaining in A-013)
- Trump card movement is **atomic** with phase transition
- Commitments are **carried forward** to Playing Tricks phase for evaluation
- Open Hand acceptance **skips** Playing Tricks phase entirely

### 7. Card Position Updates

Handle card movements per A-013 universal card map:

- **Reveal trump card**: Update visibility to all players
- **Return trump card**: Update path from `table/trump` to `player[declarerId]/hand[X]`
- **Open Hand**: Update visibility of all declarer cards to all players

## Acceptance Criteria

### Phase Transitions
- [ ] **Entry**: Automatic transition FROM Game Selection (R-005) when Trump Game selected
- [ ] **Entry**: ContractSelectionMetadata initialized correctly with declarer set
- [ ] **Exit**: Automatic transition TO Playing Tricks (R-007) after all responses complete
- [ ] **Exit**: Trump card returned to declarer's hand atomically with phase transition
- [ ] **Exit**: Commitments carried forward to Playing Tricks phase
- [ ] **Exit (Special)**: Open Hand acceptance skips to SCORING phase
- [ ] **Exit (Special)**: Open Hand rejection proceeds to Playing Tricks with cards visible

### Contract Selection Logic
- [ ] `possibleActions()` generates correct contract selection actions for each player
- [ ] `reduce()` correctly updates metadata for all contract-related actions
- [ ] `validate()` enforces all R-006 rules with clear error messages
- [ ] Doubling logic follows alternating raise rules, max 4 levels
- [ ] Defense contract duplicate prevention works
- [ ] Open Hand flow (declare → respond → auto-win or play) works correctly
- [ ] Card visibility updates correctly for trump card and Open Hand

### Testing
- [ ] Unit tests cover all actions, validation rules, and edge cases
- [ ] Integration tests verify phase transition FROM Game Selection
- [ ] Integration tests verify phase transition TO Playing Tricks
- [ ] Integration tests verify Open Hand auto-win skips Playing Tricks
- [ ] Integration tests verify commitments carried forward correctly

## Definition of Done

- [ ] Phase transition FROM Game Selection (R-005) to Contract Selection works automatically when Trump Game selected
- [ ] `ContractSelectionMetadata` defined and integrated with `GameState.metadata`
- [ ] All GameAction variants for contract selection defined (AnnounceContract, AcceptContracts, DoubleCommitment, AnnounceDefenseContract, DeclareOpenHand, RespondToOpenHand)
- [ ] `RuleSet.possibleActions()` generates correct actions for declarer and defense in all states
- [ ] `RuleSet.reduce()` correctly applies all contract actions and updates state immutably
- [ ] `RuleSet.validate()` enforces all R-006 rules with clear error messages
- [ ] Doubling logic works: 4 levels (2x, 4x, 8x, 16x), alternating raises, independent per commitment
- [ ] Defense can announce counter-contracts (max 2, no duplicates, combined scoring)
- [ ] Open Hand flow: declare → both defense respond → auto-win OR play with visible cards
- [ ] Phase transition TO Playing Tricks works automatically after all responses complete
- [ ] Trump card returned to declarer's hand atomically with phase transition
- [ ] Commitments carried forward to Playing Tricks phase for later evaluation
- [ ] Open Hand acceptance skips Playing Tricks and goes directly to SCORING
- [ ] Unit tests cover all actions, validation rules, and state transformations
- [ ] Integration tests verify complete flow: Game Selection → Contract Selection → Playing Tricks
- [ ] Integration tests verify Open Hand auto-win path

## Related

- F-016: Contract Selection and Doubling
- R-006: Contract (Commitments)
- R-005: Game Types (previous phase - entry trigger)
- R-007: Playing Tricks (next phase - exit target)
- A-013: Rule-Based Engine and Action Provider
- T-052: Contract and doubling state management (next)
- F-013: Game type selection (prerequisite feature)
