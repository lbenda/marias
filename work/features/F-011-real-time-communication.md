# F-011: Real-time Communication

* Type: Feature
* Status: Done
* Source: ADR-0012 (Real-time communication strategy)

## Description
Implement real-time game state updates using progressive enhancement:
1. WebSocket (preferred) - full duplex, separate endpoint
2. Long polling (fallback) - ETag-based with Prefer header
3. Short polling (simplest) - periodic requests, works without any headers

## Design

### Endpoints
| Transport | Endpoint |
|-----------|----------|
| Polling | `GET /games/{id}/events` |
| WebSocket | `ws://host/games/{id}/ws` |

Separate endpoints allow easier reverse proxy configuration.

### ETag Versioning
Uses `GameState.version` from Redux-style engine state:
- Numeric, increments on every state change
- Enables partial tree versioning in future

### Polling Protocol

**Request headers:**
- `If-None-Match: v{version}` - Optional, version client has
- `Prefer: wait={seconds}` - Optional, enables long polling
- `Cache-Control: no-cache` - Disable caching

**Response headers:**
- `ETag: v{version}` - Current state version
- `Cache-Control: no-store` - Prevent caching

**Behavior:**
| Request | Response |
|---------|----------|
| No `If-None-Match` | `200` + full state + `ETag` |
| `If-None-Match` matches | `304 Not Modified` |
| `If-None-Match` outdated | `200` + new state + `ETag` |
| + `Prefer: wait=30` | Hold up to 30s for changes |

### Client Implementation Levels
1. **Minimal:** Call `/events` periodically, ignore headers
2. **Optimized:** Use `If-None-Match` for 304 responses
3. **Best polling:** Add `Prefer: wait=30` for long polling
4. **Real-time:** Connect to `/ws` for WebSocket

## Success Criteria
- Polling works without any special headers (simplest client)
- ETag reduces bandwidth when no changes
- Long polling reduces request frequency
- WebSocket provides real-time updates
- All transports use same version numbering

## Related Tasks
- T-028: Server - Polling endpoint with ETag support (Done)
- T-029: Server - WebSocket endpoint (Done)
- T-030: Server - Event broadcast infrastructure (Done)
- T-031: Web UI - Transport client with fallback (Done)
- T-032: Web UI - Real-time state synchronization (Done)

## Result

Implemented full real-time communication stack:

### Server (Kotlin/Ktor)
1. **GameEventBus interface** (`server/event/GameEventBus.kt`)
   - `subscribe(gameId)`: Flow for WebSocket streaming
   - `publish(gameId, state)`: Broadcast state changes
   - `waitForChange(gameId, version, timeout)`: Long polling support

2. **InMemoryEventBus** (`server/event/InMemoryEventBus.kt`)
   - MutableSharedFlow per game for efficient pub/sub
   - Tracks latest state for immediate delivery to new subscribers

3. **Polling Endpoint** (`server/routes/EventRoutes.kt`)
   - `GET /games/{id}/events`
   - ETag versioning with `If-None-Match` header
   - Long polling with `Prefer: wait=N` header
   - Returns 304 when no changes

4. **WebSocket Endpoint** (`server/routes/WebSocketRoutes.kt`)
   - `ws://host/games/{id}/ws`
   - Sends current state on connect
   - Broadcasts state changes to all connected clients

### Web UI (React/TypeScript)
1. **Transport Client** (`api/transport/TransportClient.ts`)
   - Auto-negotiates: WebSocket -> Long Polling -> Short Polling
   - Reconnection with exponential backoff
   - Connection state tracking

2. **useTransport Hook** (`hooks/useTransport.ts`)
   - React hook for real-time game state
   - Automatic connect/disconnect lifecycle

3. **ConnectionIndicator** (`components/ConnectionIndicator.tsx`)
   - Visual indicator: Live (WS), Connected (long poll), Polling (short poll)
   - Shows reconnecting/disconnected states

4. **GamePage Integration**
   - Real-time state updates without manual refresh
   - Connection status visible to user
