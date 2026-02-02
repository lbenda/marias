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
                PlayedCard("p1", Card(Suit.SRDCE, Rank.DESITKA)),
                PlayedCard("p2", Card(Suit.SRDCE, Rank.ESO)),
                PlayedCard("p3", Card(Suit.SRDCE, Rank.KRAL))
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
                PlayedCard("p1", Card(Suit.SRDCE, Rank.ESO)),
                PlayedCard("p2", Card(Suit.ZALUDY, Rank.SEDMICKA)), // Trump
                PlayedCard("p3", Card(Suit.SRDCE, Rank.KRAL))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, Suit.ZALUDY)
        assertEquals("p2", winner) // Seven of trump beats Ace of hearts
    }

    @Test
    fun `higher trump beats lower trump`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.SRDCE, Rank.ESO)),
                PlayedCard("p2", Card(Suit.ZALUDY, Rank.SEDMICKA)),
                PlayedCard("p3", Card(Suit.ZALUDY, Rank.DESITKA))
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val winner = resolver.determineTrickWinner(trick, Suit.ZALUDY)
        assertEquals("p3", winner) // Ten of trump beats Seven of trump
    }

    @Test
    fun `off-suit card loses even if higher rank`() {
        val trick = TrickState(
            cardsPlayed = listOf(
                PlayedCard("p1", Card(Suit.SRDCE, Rank.SEDMICKA)),
                PlayedCard("p2", Card(Suit.KULE, Rank.ESO)), // Different suit, no trump
                PlayedCard("p3", Card(Suit.SRDCE, Rank.OSMICKA))
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
                PlayedCard("p1", Card(Suit.SRDCE, Rank.DESITKA)), // 10 points
                PlayedCard("p2", Card(Suit.SRDCE, Rank.ESO)),     // 11 points
                PlayedCard("p3", Card(Suit.SRDCE, Rank.KRAL))     // 4 points
            ),
            leadPlayerId = "p1",
            trickNumber = 1
        )

        val points = resolver.calculateTrickPoints(trick)
        assertEquals(25, points)
    }
}
