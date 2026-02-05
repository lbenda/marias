package cz.lbenda.games.marias.server.service

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.store.GameStore
import cz.lbenda.games.marias.server.event.GameEventBus
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameService(
    private val eventBus: GameEventBus
) {
    private val games = ConcurrentHashMap<String, GameStore>()

    fun create(playerId: String, playerName: String): GameState {
        val id = UUID.randomUUID().toString()
        val store = GameStore(GameState(gameId = id))
        games[id] = store
        val state = store.dispatchSync(GameAction.JoinGame(playerId, playerName))
        eventBus.publish(id, state)
        return state
    }

    fun get(gameId: String): GameState? = games[gameId]?.current

    fun all(): List<GameState> = games.values.map { it.current }

    fun dispatch(gameId: String, action: GameAction): GameState? {
        val state = games[gameId]?.dispatchSync(action) ?: return null
        eventBus.publish(gameId, state)
        return state
    }

    fun delete(gameId: String): Boolean {
        val removed = games.remove(gameId) != null
        if (removed) {
            eventBus.cleanup(gameId)
        }
        return removed
    }

    /** Wait for state change (used by long polling) */
    suspend fun waitForChange(gameId: String, currentVersion: Long, timeoutMs: Long): GameState? =
        eventBus.waitForChange(gameId, currentVersion, timeoutMs)

    /** Subscribe to state changes (used by WebSocket) */
    fun subscribe(gameId: String) = eventBus.subscribe(gameId)
}
