package cz.lbenda.games.marias.engine.store

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameStoreTest {

    @Test
    fun `creates with initial state`() {
        val store = GameStore(GameState(gameId = "test"))
        assertEquals("test", store.current.gameId)
        assertEquals(GamePhase.WAITING_FOR_PLAYERS, store.current.phase)
    }

    @Test
    fun `dispatch sync updates state`() {
        val store = GameStore(GameState(gameId = "test"))
        store.dispatchSync(GameAction.JoinGame("p1", "Alice"))
        assertEquals(1, store.current.players.size)
    }

    @Test
    fun `dispatch async updates state`() = runBlocking {
        val store = GameStore(GameState(gameId = "test"))
        store.dispatch(GameAction.JoinGame("p1", "Alice"))
        assertEquals(1, store.current.players.size)
    }

    @Test
    fun `multiple dispatches`() {
        val store = GameStore(GameState(gameId = "test"))
        store.dispatchSync(GameAction.JoinGame("p1", "A"))
        store.dispatchSync(GameAction.JoinGame("p2", "B"))
        store.dispatchSync(GameAction.JoinGame("p3", "C"))

        assertEquals(3, store.current.players.size)
        assertEquals(listOf("p1", "p2", "p3"), store.current.playerOrder)
    }
}
