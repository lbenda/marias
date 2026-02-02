package cz.lbenda.games.marias.engine.model

import cz.lbenda.games.marias.engine.rules.MariasCardValues
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardTest {

    @Test
    fun `standard deck has 52 cards`() {
        val deck = Card.createDeck(DeckType.STANDARD_52)
        assertEquals(52, deck.size)
    }

    @Test
    fun `piquet deck has 32 cards`() {
        val deck = Card.createDeck(DeckType.PIQUET_32)
        assertEquals(32, deck.size)
    }

    @Test
    fun `piquet deck has 4 suits with 8 cards each`() {
        val deck = Card.createDeck(DeckType.PIQUET_32)
        Suit.entries.forEach { suit ->
            val cardsOfSuit = deck.filter { it.suit == suit }
            assertEquals(8, cardsOfSuit.size, "Suit $suit should have 8 cards")
        }
    }

    @Test
    fun `piquet deck contains only ranks 7 through Ace`() {
        val deck = Card.createDeck(DeckType.PIQUET_32)
        val expectedRanks = setOf(Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE)
        val actualRanks = deck.map { it.rank }.toSet()
        assertEquals(expectedRanks, actualRanks)
    }

    @Test
    fun `marias card point values are correct`() {
        assertEquals(0, MariasCardValues.getPointValue(Rank.SEVEN))
        assertEquals(0, MariasCardValues.getPointValue(Rank.EIGHT))
        assertEquals(0, MariasCardValues.getPointValue(Rank.NINE))
        assertEquals(10, MariasCardValues.getPointValue(Rank.TEN))
        assertEquals(2, MariasCardValues.getPointValue(Rank.JACK))
        assertEquals(3, MariasCardValues.getPointValue(Rank.QUEEN))
        assertEquals(4, MariasCardValues.getPointValue(Rank.KING))
        assertEquals(11, MariasCardValues.getPointValue(Rank.ACE))
    }

    @Test
    fun `total marias deck points equals 120`() {
        // 4 suits × (11+10+4+3+2+0+0+0) = 4 × 30 = 120
        val totalPoints = MariasCardValues.getTotalDeckPoints()
        assertEquals(120, totalPoints)
    }

    @Test
    fun `marias card strength ordering is correct`() {
        val seven = Card(Suit.HEARTS, Rank.SEVEN)
        val ten = Card(Suit.HEARTS, Rank.TEN)
        val ace = Card(Suit.HEARTS, Rank.ACE)

        assertTrue(MariasCardValues.getStrength(seven) < MariasCardValues.getStrength(ten))
        assertTrue(MariasCardValues.getStrength(ten) < MariasCardValues.getStrength(ace))
    }

    @Test
    fun `cards are comparable by suit then rank ordinal`() {
        val heartAce = Card(Suit.HEARTS, Rank.ACE)      // HEARTS=3, ACE=0
        val heartSeven = Card(Suit.HEARTS, Rank.SEVEN)  // HEARTS=3, SEVEN=6
        val clubsSeven = Card(Suit.CLUBS, Rank.SEVEN)   // CLUBS=1, SEVEN=6

        // Same suit: compared by rank ordinal (ACE=0 < SEVEN=6)
        assertTrue(heartAce < heartSeven)
        // Different suits: compared by suit ordinal (CLUBS=1 < HEARTS=3)
        assertTrue(clubsSeven < heartSeven)
    }

    @Test
    fun `shuffled deck has same cards in different order`() {
        val deck1 = Card.createDeck(DeckType.PIQUET_32)
        val deck2 = Card.createShuffledDeck(DeckType.PIQUET_32)

        assertEquals(deck1.size, deck2.size)
        assertEquals(deck1.toSet(), deck2.toSet())
    }

    @Test
    fun `card toString returns short format`() {
        val card = Card(Suit.HEARTS, Rank.ACE)
        assertEquals("A♥", card.toString())
    }

    @Test
    fun `card toFullString returns descriptive format`() {
        val card = Card(Suit.HEARTS, Rank.ACE)
        assertEquals("ACE of HEARTS", card.toFullString())
    }
}
