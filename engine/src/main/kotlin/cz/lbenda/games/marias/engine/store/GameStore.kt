package cz.lbenda.games.marias.engine.store

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.reducer.GameReducer
import cz.lbenda.games.marias.engine.state.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GameStore(
    initialState: GameState,
    private val reducer: GameReducer = GameReducer()
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val mutex = Mutex()

    val currentState: GameState get() = _state.value

    suspend fun dispatch(action: GameAction): GameState {
        return mutex.withLock {
            val newState = reducer.reduce(_state.value, action)
            _state.value = newState
            newState
        }
    }

    fun dispatchSync(action: GameAction): GameState {
        val newState = reducer.reduce(_state.value, action)
        _state.value = newState
        return newState
    }

    companion object {
        fun create(gameId: String): GameStore {
            return GameStore(GameState(gameId = gameId))
        }
    }
}
