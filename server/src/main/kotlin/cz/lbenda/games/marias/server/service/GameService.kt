package cz.lbenda.games.marias.server.service

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.rules.GameRules
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.store.GameStore
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameService {
    private val games = ConcurrentHashMap<String, GameStore>()
    private val rules = GameRules()

    fun createGame(creatorPlayerId: String, creatorPlayerName: String): GameState {
        val gameId = UUID.randomUUID().toString()
        val store = GameStore.create(gameId)
        games[gameId] = store

        // Creator joins immediately
        val joinAction = GameAction.JoinGame(creatorPlayerId, creatorPlayerName)
        return store.dispatchSync(joinAction)
    }

    fun getGame(gameId: String): GameState? {
        return games[gameId]?.currentState
    }

    fun getAllGames(): List<GameState> {
        return games.values.map { it.currentState }
    }

    fun dispatchAction(gameId: String, action: GameAction): GameState? {
        val store = games[gameId] ?: return null
        return store.dispatchSync(action)
    }

    fun getPlayerHand(gameId: String, playerId: String): List<Card>? {
        val state = games[gameId]?.currentState ?: return null
        return state.players[playerId]?.hand
    }

    fun getValidCards(gameId: String, playerId: String): List<Card> {
        val state = games[gameId]?.currentState ?: return emptyList()
        return rules.getValidCards(state, playerId)
    }

    fun getTalon(gameId: String, playerId: String): List<Card>? {
        val state = games[gameId]?.currentState ?: return null
        // Only declarer can see talon during exchange
        if (state.declarerPlayerId != playerId) return null
        return state.talon
    }

    fun deleteGame(gameId: String): Boolean {
        return games.remove(gameId) != null
    }

    fun getStore(gameId: String): GameStore? = games[gameId]
}
