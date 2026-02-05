# ADR-0012: Real-time communication strategy

Status: Accepted
Date: 2026-02-05

## Decision
The server will support real-time game state updates using a progressive enhancement strategy:

1. **WebSocket** (preferred) - Full duplex communication for real-time updates
2. **Long polling** (fallback) - For environments where WebSocket is unavailable
3. **Short polling** (simplest) - Works without any special headers

Client automatically negotiates the best available transport.

## Context
Mariash is a turn-based multiplayer game requiring real-time updates:
- Players need to see opponent moves immediately
- Game state changes must be synchronized across all clients
- Different deployment environments have varying network capabilities

WebSocket provides the best user experience but may be blocked by:
- Corporate firewalls and proxies
- Some mobile networks
- Older infrastructure

A fallback strategy ensures the game works everywhere while providing optimal performance where possible.

## Technical Approach

### Endpoints
| Transport | Endpoint |
|-----------|----------|
| Polling | `GET /games/{id}/events` |
| WebSocket | `ws://host/games/{id}/ws` |

Separate endpoints allow easier reverse proxy configuration.

### ETag-based Versioning
Uses `GameState.version` from Redux-style engine state:
- Numeric version increments on every state change
- Standard HTTP conditional request semantics
- Enables partial tree versioning in future

### Polling Protocol (GET /games/{id}/events)

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

### WebSocket Protocol (ws://host/games/{id}/ws)
- On connect: receive current state immediately
- On state change: receive pushed updates
- Bidirectional: can send actions directly

### Client Implementation Levels
1. **Minimal:** Call `/events` periodically, ignore headers (short polling)
2. **Optimized:** Use `If-None-Match` for 304 responses
3. **Best polling:** Add `Prefer: wait=30` for long polling
4. **Real-time:** Connect to `/ws` for WebSocket

## Consequences
- Game works in all network environments
- Simplest client needs no special headers (just periodic GET)
- ETag reduces bandwidth when no changes (304 response)
- Long polling reduces request frequency
- WebSocket provides real-time updates
- Separate endpoints simplify reverse proxy configuration
- All transports use same version numbering from engine
