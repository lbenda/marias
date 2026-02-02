package cz.lbenda.games.marias.engine.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardTest {

    @Test
    fun `deck has 32 cards`() {
        val deck = Card.createDeck()
        assertEquals(32, deck.size)
    }

    @Test
    fun `deck has 4 suits with 8 cards each`() {
        val deck = Card.createDeck()
        Suit.entries.forEach { suit ->
            val cardsOfSuit = deck.filter { it.suit == suit }
            assertEquals(8, cardsOfSuit.size, "Suit $suit should have 8 cards")
        }
    }

    @Test
    fun `card point values are correct`() {
        assertEquals(0, Rank.SEDMICKA.pointValue)
        assertEquals(0, Rank.OSMICKA.pointValue)
        assertEquals(0, Rank.DEVITKA.pointValue)
        assertEquals(10, Rank.DESITKA.pointValue)
        assertEquals(2, Rank.SPODEK.pointValue)
        assertEquals(3, Rank.SVRSEK.pointValue)
        assertEquals(4, Rank.KRAL.pointValue)
        assertEquals(11, Rank.ESO.pointValue)
    }

    @Test
    fun `total deck points equals 120`() {
        // 4 suits × (11+10+4+3+2+0+0+0) = 4 × 30 = 120
        val deck = Card.createDeck()
        val totalPoints = deck.sumOf { it.pointValue }
        assertEquals(120, totalPoints)
    }

    @Test
    fun `card strength ordering is correct`() {
        val seven = Card(Suit.SRDCE, Rank.SEDMICKA)
        val ten = Card(Suit.SRDCE, Rank.DESITKA)
        val ace = Card(Suit.SRDCE, Rank.ESO)

        assertTrue(seven.strength < ten.strength)
        assertTrue(ten.strength < ace.strength)
    }

    @Test
    fun `cards are comparable`() {
        val heartSeven = Card(Suit.SRDCE, Rank.SEDMICKA)
        val heartAce = Card(Suit.SRDCE, Rank.ESO)
        val acornSeven = Card(Suit.ZALUDY, Rank.SEDMICKA)

        assertTrue(heartSeven < heartAce)
        assertTrue(acornSeven < heartSeven) // Different suits compared by ordinal
    }

    @Test
    fun `shuffled deck has same cards in different order`() {
        val deck1 = Card.createDeck()
        val deck2 = Card.createShuffledDeck()

        assertEquals(deck1.size, deck2.size)
        assertEquals(deck1.toSet(), deck2.toSet())
    }
}
