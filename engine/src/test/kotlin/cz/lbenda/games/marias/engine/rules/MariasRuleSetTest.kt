package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.PlayerState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MariasRuleSetTest {

    private val ruleSet = MariasRuleSet()
    private val p1 = "p1"
    private val p2 = "p2"
    private val p3 = "p3"

    @Test
    fun testWaitingForPlayersActions() {
        val state = GameState(
            gameId = "test",
            phase = GamePhase.WAITING_FOR_PLAYERS,
            players = mapOf(p1 to PlayerState(p1, "Player 1")),
            playerOrder = listOf(p1)
        )

        val actions = ruleSet.possibleActions(state, p1)
        assertTrue(actions.any { it is GameAction.LeaveGame })
        assertFalse(actions.any { it is GameAction.StartGame }) // Need 3 players

        val state3 = state.copy(
            players = mapOf(
                p1 to PlayerState(p1, "Player 1"),
                p2 to PlayerState(p2, "Player 2"),
                p3 to PlayerState(p3, "Player 3")
            ),
            playerOrder = listOf(p1, p2, p3)
        )
        val actions3 = ruleSet.possibleActions(state3, p1)
        assertTrue(actions3.any { it is GameAction.StartGame })
    }

    @Test
    fun testBiddingActions() {
        val state = GameState(
            gameId = "test",
            phase = GamePhase.BIDDING,
            players = mapOf(
                p1 to PlayerState(p1, "Player 1"),
                p2 to PlayerState(p2, "Player 2"),
                p3 to PlayerState(p3, "Player 3")
            ),
            playerOrder = listOf(p1, p2, p3),
            currentPlayerIndex = 0
        )

        val actions = ruleSet.possibleActions(state, p1)
        assertTrue(actions.any { it is GameAction.Pass })
        assertTrue(actions.any { it is GameAction.PlaceBid })

        val actionsP2 = ruleSet.possibleActions(state, p2)
        assertFalse(actionsP2.any { it is GameAction.Pass || it is GameAction.PlaceBid })
    }

    @Test
    fun testPlayingActions() {
        val hand1 = listOf(Card(Suit.HEARTS, Rank.ACE), Card(Suit.HEARTS, Rank.TEN))
        val state = GameState(
            gameId = "test",
            phase = GamePhase.PLAYING,
            players = mapOf(
                p1 to PlayerState(p1, "Player 1", hand = hand1),
                p2 to PlayerState(p2, "Player 2"),
                p3 to PlayerState(p3, "Player 3")
            ),
            playerOrder = listOf(p1, p2, p3),
            currentPlayerIndex = 0,
            trump = Suit.DIAMONDS
        )

        val actions = ruleSet.possibleActions(state, p1)
        val playActions = actions.filterIsInstance<GameAction.PlayCard>()
        assertEquals(2, playActions.size)
        assertTrue(playActions.any { it.card == Card(Suit.HEARTS, Rank.ACE) })
    }
}
