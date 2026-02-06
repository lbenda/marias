package cz.lbenda.games.marias.server.event

import cz.lbenda.games.marias.engine.state.GameState
import kotlinx.coroutines.flow.Flow

/**
 * Event bus for broadcasting game state changes to listeners.
 * Used by WebSocket connections and long-polling endpoints.
 */
interface GameEventBus {
    /**
     * Subscribe to game events, returns Flow of state changes.
     * Flow completes when game is deleted.
     */
    fun subscribe(gameId: String): Flow<GameState>

    /**
     * Publish state change. Called by GameService after action dispatch.
     */
    fun publish(gameId: String, state: GameState)

    /**
     * Wait for state change or timeout.
     * @param gameId The game to watch
     * @param currentVersion Version the client currently has
     * @param timeoutMs Maximum time to wait
     * @return New state if changed, null if timeout
     */
    suspend fun waitForChange(gameId: String, currentVersion: Long, timeoutMs: Long): GameState?

    /**
     * Clean up resources for a game (called when game is deleted).
     */
    fun cleanup(gameId: String)
}
