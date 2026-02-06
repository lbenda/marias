# T-031: Web UI - Transport Client with Fallback

- Parent: F-011
- Status: Done
- Owner: ui/web
- Related modules: ui/web
- Depends on: T-028, T-029

## Goal
Implement transport client that automatically negotiates best available
communication method: WebSocket → Long Polling → Short Polling.

## Scope
- Transport client implementation
- Transport negotiation algorithm

## API
```typescript
interface TransportClient {
  connect(gameId: string): void;
  disconnect(): void;
  onStateChange(callback: (state: GameState) => void): void;
  sendAction(action: GameAction): Promise<void>;
}

interface TransportOptions {
  preferWebSocket?: boolean;  // default: true
  longPollTimeout?: number;   // default: 30 seconds
  shortPollInterval?: number; // default: 2 seconds
  maxRetries?: number;        // default: 3
}
```

## Transport Negotiation

### Algorithm
```
1. If preferWebSocket:
   a. Try WebSocket connection
   b. If fails after timeout → fallback to long polling

2. Long polling:
   a. Send request with Prefer: wait=30
   b. If server doesn't support (no hold) → fallback to short polling
   c. On connection error → retry with backoff

3. Short polling:
   a. Poll every shortPollInterval
   b. Use If-None-Match for efficiency
```

### Connection State
```typescript
type ConnectionState =
  | 'disconnected'
  | 'connecting'
  | 'connected-websocket'
  | 'connected-longpoll'
  | 'connected-shortpoll'
  | 'reconnecting';
```

## Implementation

### WebSocket Transport
```typescript
class WebSocketTransport {
  connect(gameId: string): Promise<void>;
  onMessage(callback: (data: any) => void): void;
  send(data: any): void;
  close(): void;
}
```

### Polling Transport
```typescript
class PollingTransport {
  private version: number | null = null;

  async poll(gameId: string, longPoll: boolean): Promise<GameState | null> {
    const headers: Record<string, string> = {
      'Cache-Control': 'no-cache'
    };

    if (this.version) {
      headers['If-None-Match'] = `v${this.version}`;
    }

    if (longPoll) {
      headers['Prefer'] = 'wait=30';
    }

    const response = await fetch(`/games/${gameId}/events`, { headers });

    if (response.status === 304) {
      return null; // No changes
    }

    const state = await response.json();
    this.version = state.version;
    return state;
  }
}
```

## Files to Create
- `ui/web/src/api/transport/TransportClient.ts`
- `ui/web/src/api/transport/WebSocketTransport.ts`
- `ui/web/src/api/transport/PollingTransport.ts`
- `ui/web/src/api/transport/types.ts`

## Definition of Done
- WebSocket connection works when available
- Automatic fallback to long polling on WebSocket failure
- Automatic fallback to short polling when needed
- Reconnection with exponential backoff
- Connection state exposed to UI
- Clean disconnect handling
