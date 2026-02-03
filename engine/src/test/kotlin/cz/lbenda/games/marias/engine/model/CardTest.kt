package cz.lbenda.games.marias.engine.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardTest {

    @Test
    fun `deck has 32 cards`() {
        assertEquals(32, createDeck().size)
    }

    @Test
    fun `deck has 4 suits with 8 cards each`() {
        val deck = createDeck()
        Suit.entries.forEach { suit ->
            assertEquals(8, deck.count { it.suit == suit })
        }
    }

    @Test
    fun `card point values`() {
        assertEquals(0, Rank.SEVEN.points)
        assertEquals(10, Rank.TEN.points)
        assertEquals(2, Rank.JACK.points)
        assertEquals(3, Rank.QUEEN.points)
        assertEquals(4, Rank.KING.points)
        assertEquals(11, Rank.ACE.points)
    }

    @Test
    fun `total deck points is 120`() {
        assertEquals(120, createDeck().sumOf { it.points })
    }

    @Test
    fun `card strength ordering`() {
        assertTrue(Rank.SEVEN.strength < Rank.TEN.strength)
        assertTrue(Rank.TEN.strength < Rank.ACE.strength)
    }

    @Test
    fun `card toString`() {
        assertEquals("A♥", Card(Suit.HEARTS, Rank.ACE).toString())
        assertEquals("10♠", Card(Suit.SPADES, Rank.TEN).toString())
    }

    @Test
    fun `shuffled deck has same cards`() {
        val deck1 = createDeck()
        val deck2 = createShuffledDeck()
        assertEquals(deck1.toSet(), deck2.toSet())
    }
}
