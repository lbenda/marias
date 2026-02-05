package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameReducerTest {

    @Test
    fun `players join game`() {
        var state = GameState(gameId = "test")
        state = reduce(state, GameAction.JoinGame("p1", "Alice"))
        state = reduce(state, GameAction.JoinGame("p2", "Bob"))
        state = reduce(state, GameAction.JoinGame("p3", "Charlie"))

        assertEquals(3, state.players.size)
        assertEquals(listOf("p1", "p2", "p3"), state.playerOrder)
    }

    @Test
    fun `cannot join full game`() {
        var state = GameState(gameId = "test")
        state = reduce(state, GameAction.JoinGame("p1", "A"))
        state = reduce(state, GameAction.JoinGame("p2", "B"))
        state = reduce(state, GameAction.JoinGame("p3", "C"))
        state = reduce(state, GameAction.JoinGame("p4", "D"))

        assertEquals(3, state.players.size)
        assertNotNull(state.error)
    }

    @Test
    fun `game starts and deals`() {
        var state = setupPlayers()
        state = reduce(state, GameAction.StartGame("p1"))
        assertEquals(GamePhase.DEALING, state.phase)

        // Use non-two-phase dealing for backward compatibility
        state = reduce(state, GameAction.DealCards("p1", twoPhase = false))
        assertEquals(GamePhase.BIDDING, state.phase)
        state.players.values.forEach { assertEquals(10, it.hand.size) }
        assertEquals(2, state.talon.size)
    }

    @Test
    fun `bidding flow`() {
        var state = setupDealt()

        val bidder = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.PlaceBid(bidder, GameType.GAME))
        assertNull(state.error)

        val passer1 = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.Pass(passer1))
        assertNull(state.error)

        val passer2 = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.Pass(passer2))

        assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        assertEquals(bidder, state.declarerId)
        assertEquals(GameType.GAME, state.gameType)
    }

    @Test
    fun `version increments`() {
        var state = GameState(gameId = "test")
        assertEquals(0, state.version)

        state = reduce(state, GameAction.JoinGame("p1", "A"))
        assertEquals(1, state.version)

        state = reduce(state, GameAction.JoinGame("p2", "B"))
        assertEquals(2, state.version)
    }

    private fun setupPlayers(): GameState {
        var state = GameState(gameId = "test")
        state = reduce(state, GameAction.JoinGame("p1", "A"))
        state = reduce(state, GameAction.JoinGame("p2", "B"))
        state = reduce(state, GameAction.JoinGame("p3", "C"))
        return state
    }

    private fun setupDealt(): GameState {
        var state = setupPlayers()
        state = reduce(state, GameAction.StartGame("p1"))
        // Use non-two-phase dealing for backward compatibility
        state = reduce(state, GameAction.DealCards("p1", twoPhase = false))
        return state
    }
}
