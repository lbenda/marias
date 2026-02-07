# T-029: Server - WebSocket Endpoint

- Parent: F-011
- Status: Merged
- Owner: server
- Related modules: server
- Depends on: T-030

## Goal
Implement WebSocket endpoint for real-time game event streaming.

## Scope
Server-side implementation of WebSocket endpoint for real-time game event streaming.

## Endpoint
`ws://host/games/{id}/ws`

Separate from polling endpoint for easier reverse proxy configuration.

## Protocol

### Connection
```
GET /games/{id}/ws HTTP/1.1
Upgrade: websocket
Connection: Upgrade
```

### Server → Client Messages
```json
{
  "type": "state",
  "version": 6,
  "data": { /* GameState */ }
}
```

```json
{
  "type": "error",
  "message": "Game not found"
}
```

### Client → Server Messages (optional)
```json
{
  "type": "action",
  "action": { /* GameAction */ }
}
```

## Behavior

### On Connect
1. Validate game exists
2. Send current state immediately
3. Subscribe to game events

### On Game State Change
1. Broadcast new state to all connected clients
2. Include version number for client reconciliation

### On Disconnect
1. Unsubscribe from game events
2. Clean up connection resources

### On Client Action (optional)
1. Validate and dispatch action to engine
2. State change will be broadcast via normal mechanism

## Implementation Notes
- Use Ktor WebSocket support
- Maintain set of active connections per game
- Handle connection errors gracefully
- Consider heartbeat/ping for connection health

## Files to Create/Modify
- `server/src/main/kotlin/cz/lbenda/games/marias/server/routes/WebSocketRoutes.kt`
- `server/src/main/kotlin/cz/lbenda/games/marias/server/service/ConnectionManager.kt`
- Update `Application.kt` with WebSocket plugin

## Definition of Done
- WebSocket connection established successfully
- Current state sent on connect
- State changes broadcast to all connected clients
- Clean disconnect handling
- Connection errors don't crash server
- Tests cover connect/disconnect/broadcast
