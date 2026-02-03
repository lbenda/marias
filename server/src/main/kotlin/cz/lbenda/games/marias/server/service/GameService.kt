package cz.lbenda.games.marias.server.service

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.store.GameStore
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameService {
    private val games = ConcurrentHashMap<String, GameStore>()

    fun create(playerId: String, playerName: String): GameState {
        val id = UUID.randomUUID().toString()
        val store = GameStore(GameState(gameId = id))
        games[id] = store
        return store.dispatchSync(GameAction.JoinGame(playerId, playerName))
    }

    fun get(gameId: String): GameState? = games[gameId]?.current

    fun all(): List<GameState> = games.values.map { it.current }

    fun dispatch(gameId: String, action: GameAction): GameState? =
        games[gameId]?.dispatchSync(action)

    fun delete(gameId: String): Boolean = games.remove(gameId) != null
}
