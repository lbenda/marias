# T-032: Web UI - Real-time State Synchronization

- Parent: F-011
- Status: Planned
- Owner: ui/web
- Related modules: ui/web
- Depends on: T-031

## Goal
Integrate transport client with React UI for real-time game state updates
and optimistic UI updates with server reconciliation.

## React Integration

### useGameState Hook
```typescript
interface UseGameStateResult {
  state: GameState | null;
  loading: boolean;
  error: Error | null;
  connectionStatus: ConnectionState;
  sendAction: (action: GameAction) => Promise<void>;
}

function useGameState(gameId: string): UseGameStateResult;
```

### Usage in Components
```typescript
function GamePage({ gameId }: { gameId: string }) {
  const { state, loading, error, connectionStatus, sendAction } = useGameState(gameId);

  if (loading) return <Loading />;
  if (error) return <Error message={error.message} />;
  if (!state) return <NotFound />;

  return (
    <div>
      <ConnectionIndicator status={connectionStatus} />
      <GameBoard state={state} onAction={sendAction} />
    </div>
  );
}
```

## State Synchronization

### Version Tracking
- Store current version from server
- Detect version gaps (missed updates)
- Request full state refresh on gap detection

### Optimistic Updates
```typescript
async function sendAction(action: GameAction) {
  // 1. Apply optimistic update locally
  const optimisticState = applyAction(state, action);
  setLocalState(optimisticState);

  try {
    // 2. Send to server
    await api.dispatch(gameId, action);
    // 3. Server state will arrive via transport
  } catch (error) {
    // 4. Rollback on error
    setLocalState(state);
    throw error;
  }
}
```

### Reconciliation
When server state arrives:
1. Compare server version with local version
2. If server is newer, replace local state
3. If versions match, state is consistent

## Connection Status UI

### ConnectionIndicator Component
```typescript
function ConnectionIndicator({ status }: { status: ConnectionState }) {
  const indicators = {
    'disconnected': { color: 'red', label: 'Disconnected' },
    'connecting': { color: 'yellow', label: 'Connecting...' },
    'connected-websocket': { color: 'green', label: 'Live' },
    'connected-longpoll': { color: 'green', label: 'Connected' },
    'connected-shortpoll': { color: 'orange', label: 'Polling' },
    'reconnecting': { color: 'yellow', label: 'Reconnecting...' },
  };
  // ...
}
```

## Files to Create
- `ui/web/src/hooks/useGameState.ts`
- `ui/web/src/hooks/useTransport.ts`
- `ui/web/src/components/ConnectionIndicator.tsx`

## Files to Modify
- `ui/web/src/pages/GamePage.tsx` - integrate useGameState

## Definition of Done
- useGameState hook provides reactive game state
- UI updates immediately on server state change
- Connection status visible to user
- Optimistic updates for responsive feel
- Graceful handling of connection loss
- State reconciliation on reconnect
