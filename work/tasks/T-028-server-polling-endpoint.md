# T-028: Server - Polling Endpoint with ETag Support

- Parent: F-011
- Status: Merged
- Owner: server
- Related modules: server
- Depends on: none

## Goal
Implement polling endpoint for game events with ETag-based conditional requests.

## Scope
- Implement polling endpoint for game events

## Endpoint
`GET /games/{id}/events`

## Request Headers
| Header | Required | Description |
|--------|----------|-------------|
| `If-None-Match` | No | Version client has (e.g., `v5`) |
| `Prefer` | No | `wait={seconds}` for long polling |
| `Cache-Control` | No | Should be `no-cache` |

## Response

### 200 OK (new data or no If-None-Match)
```
HTTP/1.1 200 OK
ETag: v6
Cache-Control: no-store
Content-Type: application/json

{
  "version": 6,
  "gameId": "abc123",
  "phase": "PLAYING",
  ...
}
```

### 304 Not Modified (version matches)
```
HTTP/1.1 304 Not Modified
ETag: v5
Cache-Control: no-store
```

## Behavior

### Without If-None-Match
Return current game state immediately with ETag.

### With If-None-Match (short polling)
- If version matches: return 304
- If version outdated: return 200 with current state

### With If-None-Match + Prefer: wait=N (long polling)
- If version outdated: return 200 immediately
- If version matches: wait up to N seconds for change
- On change: return 200 with new state
- On timeout: return 304

## Implementation Notes
- Parse `If-None-Match` header, extract version number
- Parse `Prefer` header for wait time (default: 0 = short polling)
- Use coroutine suspension for long polling wait
- Maximum wait time should be configurable (e.g., 60s cap)

## Files to Create/Modify
- `server/src/main/kotlin/cz/lbenda/games/marias/server/routes/EventRoutes.kt`
- Update `server/src/main/kotlin/cz/lbenda/games/marias/server/Application.kt`

## Definition of Done
- Endpoint returns current state without If-None-Match
- Endpoint returns 304 when version matches (short polling)
- Endpoint returns 200 when version outdated
- Endpoint waits for changes with Prefer header (long polling)
- Cache-Control: no-store in all responses
- Tests cover all scenarios
