package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.TrickState
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrickResolverTest {

    @Test
    fun `highest of lead suit wins`() {
        val trick = TrickState(
            cards = listOf(
                "p1" to Card(Suit.HEARTS, Rank.TEN),
                "p2" to Card(Suit.HEARTS, Rank.ACE),
                "p3" to Card(Suit.HEARTS, Rank.KING)
            ),
            leadPlayerId = "p1"
        )
        assertEquals("p2", determineTrickWinner(trick, null))
    }

    @Test
    fun `trump beats lead suit`() {
        val trick = TrickState(
            cards = listOf(
                "p1" to Card(Suit.HEARTS, Rank.ACE),
                "p2" to Card(Suit.CLUBS, Rank.SEVEN),
                "p3" to Card(Suit.HEARTS, Rank.KING)
            ),
            leadPlayerId = "p1"
        )
        assertEquals("p2", determineTrickWinner(trick, Suit.CLUBS))
    }

    @Test
    fun `higher trump wins`() {
        val trick = TrickState(
            cards = listOf(
                "p1" to Card(Suit.HEARTS, Rank.ACE),
                "p2" to Card(Suit.CLUBS, Rank.SEVEN),
                "p3" to Card(Suit.CLUBS, Rank.TEN)
            ),
            leadPlayerId = "p1"
        )
        assertEquals("p3", determineTrickWinner(trick, Suit.CLUBS))
    }

    @Test
    fun `trick points`() {
        val trick = TrickState(
            cards = listOf(
                "p1" to Card(Suit.HEARTS, Rank.TEN),   // 10
                "p2" to Card(Suit.HEARTS, Rank.ACE),   // 11
                "p3" to Card(Suit.HEARTS, Rank.KING)   // 4
            ),
            leadPlayerId = "p1"
        )
        assertEquals(25, trickPoints(trick))
    }
}
