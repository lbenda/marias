package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.PlayedCard
import cz.lbenda.games.marias.engine.state.TrickState
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrickResolverTest {

    private val resolver = TrickResolver()

    @Test
    fun `highest card of lead suit wins without trump`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.HEARTS, Rank.TEN)),
                PlayedCard("p2", Card(Suit.HEARTS, Rank.ACE)),
                PlayedCard("p3", Card(Suit.HEARTS, Rank.KING))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, null)
        assertEquals("p2", winner) // Ace is highest
    }

    @Test
    fun `trump beats higher card of lead suit`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.HEARTS, Rank.ACE)),
                PlayedCard("p2", Card(Suit.CLUBS, Rank.SEVEN)), // Trump
                PlayedCard("p3", Card(Suit.HEARTS, Rank.KING))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, Suit.CLUBS)
        assertEquals("p2", winner) // Seven of trump beats Ace of hearts
    }

    @Test
    fun `higher trump beats lower trump`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.HEARTS, Rank.ACE)),
                PlayedCard("p2", Card(Suit.CLUBS, Rank.SEVEN)),
                PlayedCard("p3", Card(Suit.CLUBS, Rank.TEN))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, Suit.CLUBS)
        assertEquals("p3", winner) // Ten of trump beats Seven of trump
    }

    @Test
    fun `off-suit card loses even if higher rank`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.HEARTS, Rank.SEVEN)),
                PlayedCard("p2", Card(Suit.DIAMONDS, Rank.ACE)), // Different suit, no trump
                PlayedCard("p3", Card(Suit.HEARTS, Rank.EIGHT))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, null)
        assertEquals("p3", winner) // Eight of hearts beats because it follows suit
    }

    @Test
    fun `trick points calculated correctly`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.HEARTS, Rank.TEN)),  // 10 points
                PlayedCard("p2", Card(Suit.HEARTS, Rank.ACE)), // 11 points
                PlayedCard("p3", Card(Suit.HEARTS, Rank.KING)) // 4 points
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val points = resolver.calculateTrickPoints(trick)
        assertEquals(25, points)
    }
}
