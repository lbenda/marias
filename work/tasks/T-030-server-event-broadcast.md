# T-030: Server - Event Broadcast Infrastructure

- Parent: F-011
- Status: Done
- Owner: server
- Related modules: server
- Depends on: none

## Goal
Implement infrastructure for broadcasting game state changes to all listeners
(WebSocket connections and long-polling waiters).

## Scope
- Implement event bus interface
- Implement in-memory event bus implementation
- Integrate event bus with GameService

## Components

### GameEventBus
Central event bus for game state changes:
```kotlin
interface GameEventBus {
    /** Subscribe to game events, returns Flow of state changes */
    fun subscribe(gameId: String): Flow<GameState>

    /** Publish state change (called by GameService after action) */
    fun publish(gameId: String, state: GameState)

    /** Wait for state change or timeout, returns new state or null */
    suspend fun waitForChange(gameId: String, currentVersion: Long, timeoutMs: Long): GameState?
}
```

### Integration with GameService
```kotlin
class GameService(
    private val eventBus: GameEventBus
) {
    fun dispatch(gameId: String, action: GameAction): GameState {
        val newState = store.dispatch(action)
        eventBus.publish(gameId, newState)
        return newState
    }
}
```

## Implementation

### In-Memory Event Bus
For single-server deployment:
- Use `MutableSharedFlow` per game
- `waitForChange` uses `withTimeoutOrNull`
- Clean up flows when game deleted

### Future: Distributed Event Bus
For multi-server deployment (not in scope):
- Redis pub/sub
- Kafka
- etc.

## Usage

### By Long Polling Endpoint (T-028)
```kotlin
val newState = eventBus.waitForChange(gameId, clientVersion, waitMs)
if (newState != null) {
    call.respond(newState)
} else {
    call.respond(HttpStatusCode.NotModified)
}
```

### By WebSocket Endpoint (T-029)
```kotlin
eventBus.subscribe(gameId).collect { state ->
    session.send(Frame.Text(json.encodeToString(state)))
}
```

## Files to Create
- `server/src/main/kotlin/cz/lbenda/games/marias/server/event/GameEventBus.kt`
- `server/src/main/kotlin/cz/lbenda/games/marias/server/event/InMemoryEventBus.kt`

## Files to Modify
- `server/src/main/kotlin/cz/lbenda/games/marias/server/service/GameService.kt`

## Definition of Done
- GameEventBus interface defined
- InMemoryEventBus implementation working
- GameService publishes events after state changes
- waitForChange returns on state change or timeout
- subscribe provides Flow for WebSocket streaming
- Tests cover publish/subscribe/timeout scenarios
