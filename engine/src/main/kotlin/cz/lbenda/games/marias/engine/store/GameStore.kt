package cz.lbenda.games.marias.engine.store

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.reducer.reduce
import cz.lbenda.games.marias.engine.state.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GameStore(initial: GameState) {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<GameState> = _state
    val current: GameState get() = _state.value

    private val mutex = Mutex()

    suspend fun dispatch(action: GameAction): GameState = mutex.withLock {
        _state.value = reduce(_state.value, action)
        _state.value
    }

    fun dispatchSync(action: GameAction): GameState {
        _state.value = reduce(_state.value, action)
        return _state.value
    }
}
