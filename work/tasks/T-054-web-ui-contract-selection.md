# T-054: Data-driven UI for contract selection

- Type: Task
- Status: Todo
- Feature: F-016
- Source: R-006
- Architecture: A-013 (Rule-Based Engine and Action Provider)
- Depends on: T-053

## Goal

Implement a thin, data-driven UI for the Contract Selection phase that renders all controls dynamically from `possibleActions` provided by the server, following A-013's Action Provider pattern with zero client-side game logic.

## Scope

**IN:**
- ContractSelectionView component for CONTRACT_SELECTION phase
- Dynamic action button rendering from `possibleActions` list
- Action submission via unified `/games/{gameId}/actions` endpoint
- Commitments summary display (declarer + defense contracts with multipliers)
- Turn indicator showing current responding player
- Open Hand flow UI (declare, show cards, defense responds)
- Phase transition handling (to Playing Tricks phase)
- Error display from server validation failures
- Real-time state updates if F-011 available
- Responsive design (desktop, tablet, mobile)
- Accessibility (keyboard navigation, screen readers)

**OUT:**
- Client-side validation (server provides only valid actions)
- Game rule logic (all in engine, accessed via `possibleActions`)
- Server API implementation (covered in T-053)
- Engine logic (covered in T-051)

## Objective

Implement a **data-driven UI** for the Contract Selection phase following A-013's Action Provider pattern. The UI renders buttons and controls directly from `possibleActions` provided by the server, eliminating client-side game logic.

## Requirements

### 1. Action-Driven Rendering

Per A-013, the UI is **thin** and **data-driven**:

**Core Principle:**
- Fetch `GameState` from server (includes `possibleActions` for current player)
- Render buttons/controls for each action in `possibleActions`
- On button click, submit that action to server
- Receive new `GameState` with updated `possibleActions`
- Re-render

**No client-side validation** - if an action is in `possibleActions`, it's valid and should be rendered as an enabled button.

### 2. Contract Selection View Component

Create a generic view component for CONTRACT_SELECTION phase:

```jsx
<ContractSelectionView gameState={gameState} onAction={submitAction}>
  <TrumpCardDisplay card={extractTrumpCard(gameState)} />
  <CommitmentsSummary metadata={gameState.metadata} />
  <ActionButtons
    actions={gameState.possibleActions}
    onActionClick={submitAction}
  />
  <TurnIndicator currentPlayer={gameState.metadata.currentRespondingPlayerId} />
</ContractSelectionView>
```

### 3. Action Button Rendering

Render buttons dynamically from `possibleActions`:

```jsx
function ActionButtons({ actions, onActionClick }) {
  return actions.map(action => (
    <button
      key={action.actionType + JSON.stringify(action.payload)}
      onClick={() => onActionClick(action)}
      className="action-button"
    >
      {action.displayName}
    </button>
  ));
}
```

**Examples:**
- `{ actionType: "AnnounceContract", displayName: "Announce Game", payload: { contract: "GAME" } }`
  → Button: "Announce Game"

- `{ actionType: "DoubleCommitment", displayName: "Double declarer's Game (2x → 4x)", payload: { commitmentId: "decl-1" } }`
  → Button: "Double declarer's Game (2x → 4x)"

- `{ actionType: "AcceptContracts", displayName: "Good", payload: {} }`
  → Button: "Good"

### 4. Commitments Summary Display

Display current commitments from `metadata`:

```jsx
function CommitmentsSummary({ metadata }) {
  return (
    <div className="commitments">
      <div className="declarer-commitment">
        <strong>Declarer:</strong> {metadata.declarerCommitment.contractType}
        <span className="multiplier">{getMultiplier(metadata.declarerCommitment.doublingLevel)}x</span>
      </div>
      {metadata.defenseCommitments.map(c => (
        <div key={c.id} className="defense-commitment">
          <strong>{c.owner}:</strong> {c.contractType}
          <span className="multiplier">{getMultiplier(c.doublingLevel)}x</span>
        </div>
      ))}
    </div>
  );
}
```

### 5. Action Submission

Submit actions via unified endpoint:

```javascript
async function submitAction(action) {
  const response = await fetch(`/games/${gameId}/actions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      actionType: action.actionType,
      payload: action.payload
    })
  });

  const result = await response.json();
  if (result.success) {
    // Update game state in React state/context
    setGameState(result.gameState);
  } else {
    // Show error to user
    showError(result.error.message);
  }
}
```

### 6. Open Hand Display

When `metadata.openHandState` exists:

**Declarer (who declared Open Hand):**
- Show "Waiting for defense to respond..."
- Display own cards (already visible via `cardMap` with visibility)

**Defense:**
- Show declarer's revealed cards
- Render action buttons from `possibleActions`:
  - `{ actionType: "RespondToOpenHand", displayName: "Accept", payload: { accept: true } }`
  - `{ actionType: "RespondToOpenHand", displayName: "Play", payload: { accept: false } }`

### 7. Turn Indicator

Show whose turn it is using `metadata.currentRespondingPlayerId`:

```jsx
function TurnIndicator({ currentPlayer, allPlayers }) {
  return (
    <div className="turn-indicator">
      {allPlayers.map(p => (
        <div
          key={p.id}
          className={p.id === currentPlayer ? 'active' : 'inactive'}
        >
          {p.name} {p.id === currentPlayer && '👈'}
        </div>
      ))}
    </div>
  );
}
```

### 8. Phase Transition Handling

When `gameState.phase` changes from `CONTRACT_SELECTION` to `PLAYING_TRICKS`:
- Display brief transition message: "Contract selection complete. Starting play..."
- Animate trump card returning to declarer's hand (optional)
- Switch to trick-taking view component

### 9. Error Handling

Display server errors from action submission:

```jsx
function ErrorDisplay({ error }) {
  if (!error) return null;
  return (
    <div className="error-message">
      <strong>{error.code}:</strong> {error.message}
    </div>
  );
}
```

### 10. Real-Time Updates

If F-011 (real-time communication) is available:
- Subscribe to game state updates via WebSocket/SSE
- Update local game state when other players act
- Show toast notification: "Player X doubled your contract"

### 11. Styling and UX

Apply consistent styling:
- Use existing game theme colors and typography
- Highlight clickable buttons with hover effects
- Disable button styling for actions not in `possibleActions`
- Animate commitments appearing/updating
- Use card suit symbols (♠♣♦♥) where appropriate
- Mobile-friendly touch targets (minimum 44x44px)

### 12. Accessibility

Ensure accessibility:
- Keyboard navigation for all action buttons
- ARIA labels for screen readers
- Focus indicators on interactive elements
- Announce phase changes to screen readers
- High contrast mode support

## Acceptance Criteria

- [ ] ContractSelectionView component renders for CONTRACT_SELECTION phase
- [ ] Action buttons rendered dynamically from `possibleActions` list
- [ ] All buttons submit actions via unified `/games/{gameId}/actions` endpoint
- [ ] No client-side game logic - UI driven entirely by server data
- [ ] Commitments summary displays all active commitments with multipliers
- [ ] Turn indicator shows current responding player clearly
- [ ] Open Hand flow works (declare, show cards, defense responds)
- [ ] Phase transition to PLAYING_TRICKS displays smoothly
- [ ] Error messages from server displayed clearly to user
- [ ] Real-time updates work (if F-011 available)
- [ ] UI is responsive (desktop, tablet, mobile)
- [ ] Keyboard navigation and screen reader support working
- [ ] Consistent with existing UI/UX patterns in the app

## Definition of Done

- [ ] ContractSelectionView component renders when `gameState.phase === "CONTRACT_SELECTION"`
- [ ] Action buttons rendered dynamically from `possibleActions` array
- [ ] Each button displays `action.displayName` and submits correct payload on click
- [ ] Action submission uses unified `/games/{gameId}/actions` endpoint
- [ ] No client-side validation - all buttons come from `possibleActions`
- [ ] Commitments summary displays declarer and defense contracts with multipliers
- [ ] Doubling level displayed correctly (1x, 2x, 4x, 8x, 16x)
- [ ] Turn indicator highlights current responding player
- [ ] Trump card displayed prominently from `cardMap`
- [ ] Open Hand declaration flow: button → shows cards → defense responds
- [ ] Open Hand cards displayed from `cardMap` with visibility rules
- [ ] Phase transition to PLAYING_TRICKS displays transition message
- [ ] Error messages from server displayed clearly to user
- [ ] Real-time updates work if F-011 available (WebSocket/SSE)
- [ ] Responsive layout works on desktop, tablet, and mobile
- [ ] Keyboard navigation works for all interactive elements
- [ ] Screen reader accessibility with ARIA labels
- [ ] Component consistent with existing UI/UX patterns

## Related

- F-016: Contract Selection and Doubling
- R-006: Contract (Commitments)
- A-013: Rule-Based Engine and Action Provider
- T-053: Server API for contract selection
- T-006: Web UI prototype REST game flow (reference for existing patterns)
- F-015: Generic UI Client (may provide reusable components)
