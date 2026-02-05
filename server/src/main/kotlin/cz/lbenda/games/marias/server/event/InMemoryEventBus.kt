package cz.lbenda.games.marias.server.event

import cz.lbenda.games.marias.engine.state.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of GameEventBus for single-server deployment.
 * Uses MutableSharedFlow for efficient pub/sub.
 */
class InMemoryEventBus : GameEventBus {
    // One flow per game, created lazily
    private val flows = ConcurrentHashMap<String, MutableSharedFlow<GameState>>()

    // Track latest state per game for new subscribers
    private val latestStates = ConcurrentHashMap<String, GameState>()

    private fun getOrCreateFlow(gameId: String): MutableSharedFlow<GameState> =
        flows.computeIfAbsent(gameId) {
            MutableSharedFlow(replay = 1, extraBufferCapacity = 16)
        }

    override fun subscribe(gameId: String): Flow<GameState> =
        getOrCreateFlow(gameId)

    override fun publish(gameId: String, state: GameState) {
        latestStates[gameId] = state
        val flow = getOrCreateFlow(gameId)
        flow.tryEmit(state)
    }

    override suspend fun waitForChange(
        gameId: String,
        currentVersion: Long,
        timeoutMs: Long
    ): GameState? {
        // First check if we already have a newer version
        val latest = latestStates[gameId]
        if (latest != null && latest.version > currentVersion) {
            return latest
        }

        // Wait for a new state with higher version
        val flow = getOrCreateFlow(gameId)
        return withTimeoutOrNull(timeoutMs) {
            flow.filter { it.version > currentVersion }.first()
        }
    }

    override fun cleanup(gameId: String) {
        flows.remove(gameId)
        latestStates.remove(gameId)
    }
}
