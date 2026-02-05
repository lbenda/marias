package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.reducer.reduce
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType
import org.junit.jupiter.api.Test
import kotlin.test.*

class TalonValidationTest {

    @Test
    fun `isValidTalonDiscard allows low cards in normal game`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.SEVEN),
            Card(Suit.SPADES, Rank.EIGHT)
        )
        assertTrue(isValidTalonDiscard(cards, GameType.GAME))
    }

    @Test
    fun `isValidTalonDiscard rejects Ace in normal game`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN)
        )
        assertFalse(isValidTalonDiscard(cards, GameType.GAME))
    }

    @Test
    fun `isValidTalonDiscard rejects Ten in normal game`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.SEVEN)
        )
        assertFalse(isValidTalonDiscard(cards, GameType.GAME))
    }

    @Test
    fun `isValidTalonDiscard rejects Ace in Hundred game`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.SEVEN)
        )
        assertFalse(isValidTalonDiscard(cards, GameType.HUNDRED))
    }

    @Test
    fun `isValidTalonDiscard allows Ace in Misere`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.TEN)
        )
        assertTrue(isValidTalonDiscard(cards, GameType.MISERE))
    }

    @Test
    fun `isValidTalonDiscard allows Ten in Slam`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.TEN),
            Card(Suit.SPADES, Rank.ACE)
        )
        assertTrue(isValidTalonDiscard(cards, GameType.SLAM))
    }

    @Test
    fun `isValidTalonDiscard allows Ace and Ten in Misere`() {
        val cards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.SPADES, Rank.TEN)
        )
        assertTrue(isValidTalonDiscard(cards, GameType.MISERE))
    }

    @Test
    fun `exchange talon with low cards succeeds`() {
        var state = setupTalonExchangePhase()
        val declarer = state.declarerId!!
        val hand = state.players[declarer]!!.hand

        // Find two low cards (not Ace or Ten)
        val lowCards = hand.filter { it.rank != Rank.ACE && it.rank != Rank.TEN }.take(2)
        assertTrue(lowCards.size >= 2, "Need at least 2 low cards for test, hand: $hand")

        state = reduce(state, GameAction.ExchangeTalon(declarer, lowCards))

        assertNull(state.error, "Unexpected error: ${state.error}, lowCards: $lowCards, gameType: ${state.gameType}")
        // After normal bidding, goes to TRUMP_SELECTION (trump not set yet)
        assertEquals(GamePhase.TRUMP_SELECTION, state.phase)
    }

    @Test
    fun `exchange talon with Ace is rejected`() {
        var state = setupTalonExchangePhase()
        val declarer = state.declarerId!!
        val hand = state.players[declarer]!!.hand

        // Find an Ace and a low card
        val ace = hand.find { it.rank == Rank.ACE }
        val lowCard = hand.find { it.rank != Rank.ACE && it.rank != Rank.TEN }

        if (ace != null && lowCard != null) {
            state = reduce(state, GameAction.ExchangeTalon(declarer, listOf(ace, lowCard)))
            assertEquals("Cannot discard Ace or Ten to talon", state.error)
            assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        }
    }

    @Test
    fun `exchange talon with Ten is rejected`() {
        var state = setupTalonExchangePhase()
        val declarer = state.declarerId!!
        val hand = state.players[declarer]!!.hand

        // Find a Ten and a low card
        val ten = hand.find { it.rank == Rank.TEN }
        val lowCard = hand.find { it.rank != Rank.ACE && it.rank != Rank.TEN }

        if (ten != null && lowCard != null) {
            state = reduce(state, GameAction.ExchangeTalon(declarer, listOf(ten, lowCard)))
            assertEquals("Cannot discard Ace or Ten to talon", state.error)
            assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        }
    }

    private fun setupTalonExchangePhase(): GameState {
        var state = GameState(gameId = "test")
        state = reduce(state, GameAction.JoinGame("p1", "Alice"))
        state = reduce(state, GameAction.JoinGame("p2", "Bob"))
        state = reduce(state, GameAction.JoinGame("p3", "Charlie"))
        state = reduce(state, GameAction.StartGame("p1"))
        state = reduce(state, GameAction.DealCards("p1", twoPhase = false))

        // Bidding: p2 bids, others pass
        val bidder = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.PlaceBid(bidder, GameType.GAME))

        val passer1 = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.Pass(passer1))

        val passer2 = state.playerOrder[state.currentPlayerIndex]
        state = reduce(state, GameAction.Pass(passer2))

        assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        return state
    }
}
