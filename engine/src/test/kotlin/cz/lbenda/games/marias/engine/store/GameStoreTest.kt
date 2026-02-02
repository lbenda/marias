package cz.lbenda.games.marias.engine.store

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GamePhase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameStoreTest {

    @Test
    fun `store creates with initial state`() {
        val store = GameStore.create("test-game")

        assertEquals("test-game", store.currentState.gameId)
        assertEquals(GamePhase.WAITING_FOR_PLAYERS, store.currentState.phase)
        assertEquals(0, store.currentState.players.size)
    }

    @Test
    fun `dispatch updates state synchronously`() {
        val store = GameStore.create("test-game")

        store.dispatchSync(GameAction.JoinGame("p1", "Player 1"))

        assertEquals(1, store.currentState.players.size)
        assertNotNull(store.currentState.players["p1"])
    }

    @Test
    fun `dispatch updates state asynchronously`() = runBlocking {
        val store = GameStore.create("test-game")

        store.dispatch(GameAction.JoinGame("p1", "Player 1"))

        assertEquals(1, store.currentState.players.size)
    }

    @Test
    fun `state flow emits updates`() = runBlocking {
        val store = GameStore.create("test-game")

        // Get initial state
        val initialState = store.state.first()
        assertEquals(0, initialState.players.size)

        // Dispatch and check new state
        store.dispatch(GameAction.JoinGame("p1", "Player 1"))
        val newState = store.state.first()

        assertEquals(1, newState.players.size)
    }

    @Test
    fun `multiple dispatches maintain consistency`() {
        val store = GameStore.create("test-game")

        store.dispatchSync(GameAction.JoinGame("p1", "Player 1"))
        store.dispatchSync(GameAction.JoinGame("p2", "Player 2"))
        store.dispatchSync(GameAction.JoinGame("p3", "Player 3"))

        assertEquals(3, store.currentState.players.size)
        assertEquals(listOf("p1", "p2", "p3"), store.currentState.playerOrder)
    }
}
