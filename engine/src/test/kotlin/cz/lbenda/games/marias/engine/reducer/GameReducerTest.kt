package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameReducerTest {

    private val reducer = GameReducer()

    @Test
    fun `players can join game`() {
        val state = GameState(gameId = "test-game")

        val state1 = reducer.reduce(state, GameAction.JoinGame("p1", "Player 1"))
        assertEquals(1, state1.players.size)
        assertEquals("Player 1", state1.players["p1"]?.name)

        val state2 = reducer.reduce(state1, GameAction.JoinGame("p2", "Player 2"))
        assertEquals(2, state2.players.size)

        val state3 = reducer.reduce(state2, GameAction.JoinGame("p3", "Player 3"))
        assertEquals(3, state3.players.size)
        assertTrue(state3.isGameFull)
    }

    @Test
    fun `cannot join full game`() {
        var state = GameState(gameId = "test-game")
        state = reducer.reduce(state, GameAction.JoinGame("p1", "Player 1"))
        state = reducer.reduce(state, GameAction.JoinGame("p2", "Player 2"))
        state = reducer.reduce(state, GameAction.JoinGame("p3", "Player 3"))

        val finalState = reducer.reduce(state, GameAction.JoinGame("p4", "Player 4"))
        assertNotNull(finalState.errorMessage)
        assertEquals(3, finalState.players.size)
    }

    @Test
    fun `game starts with 3 players`() {
        var state = GameState(gameId = "test-game")
        state = reducer.reduce(state, GameAction.JoinGame("p1", "Player 1"))
        state = reducer.reduce(state, GameAction.JoinGame("p2", "Player 2"))
        state = reducer.reduce(state, GameAction.JoinGame("p3", "Player 3"))

        state = reducer.reduce(state, GameAction.StartGame("p1"))
        assertEquals(GamePhase.DEALING, state.phase)
    }

    @Test
    fun `cards are dealt correctly`() {
        var state = setupGameWithPlayers()
        state = reducer.reduce(state, GameAction.StartGame("p1"))
        state = reducer.reduce(state, GameAction.DealCards("p1"))

        assertEquals(GamePhase.BIDDING, state.phase)

        // Each player has 10 cards
        state.players.values.forEach { player ->
            assertEquals(10, player.hand.size, "Player ${player.name} should have 10 cards")
        }

        // Talon has 2 cards
        assertEquals(2, state.talon.size)
    }

    @Test
    fun `bidding advances to next player`() {
        var state = setupGameWithDealtCards()

        val currentPlayer = state.currentPlayerId!!
        state = reducer.reduce(state, GameAction.PlaceBid(currentPlayer, GameType.HRA))

        assertNull(state.errorMessage)
        assertTrue(state.currentPlayerId != currentPlayer || state.biddingState.passedPlayers.isNotEmpty())
    }

    @Test
    fun `passing works correctly`() {
        var state = setupGameWithDealtCards()

        val firstBidder = state.currentPlayerId!!
        state = reducer.reduce(state, GameAction.Pass(firstBidder))

        assertNull(state.errorMessage)
        assertTrue(firstBidder in state.biddingState.passedPlayers)
    }

    @Test
    fun `bidding completes when two players pass`() {
        var state = setupGameWithDealtCards()

        // First player bids
        val p1 = state.currentPlayerId!!
        state = reducer.reduce(state, GameAction.PlaceBid(p1, GameType.HRA))

        // Second player passes
        val p2 = state.currentPlayerId!!
        state = reducer.reduce(state, GameAction.Pass(p2))

        // Third player passes
        val p3 = state.currentPlayerId!!
        state = reducer.reduce(state, GameAction.Pass(p3))

        assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        assertEquals(p1, state.declarerPlayerId)
        assertEquals(GameType.HRA, state.gameType)
    }

    @Test
    fun `version increments on each action`() {
        var state = GameState(gameId = "test-game")
        assertEquals(0, state.version)

        state = reducer.reduce(state, GameAction.JoinGame("p1", "Player 1"))
        assertEquals(1, state.version)

        state = reducer.reduce(state, GameAction.JoinGame("p2", "Player 2"))
        assertEquals(2, state.version)
    }

    private fun setupGameWithPlayers(): GameState {
        var state = GameState(gameId = "test-game")
        state = reducer.reduce(state, GameAction.JoinGame("p1", "Player 1"))
        state = reducer.reduce(state, GameAction.JoinGame("p2", "Player 2"))
        state = reducer.reduce(state, GameAction.JoinGame("p3", "Player 3"))
        return state
    }

    private fun setupGameWithDealtCards(): GameState {
        var state = setupGameWithPlayers()
        state = reducer.reduce(state, GameAction.StartGame("p1"))
        state = reducer.reduce(state, GameAction.DealCards("p1"))
        return state
    }
}
