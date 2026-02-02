package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeckUtilsTest {

    @Test
    fun `deal cards distributes correctly`() {
        val deck = Card.createDeck()
        val players = listOf("p1", "p2", "p3")

        val (hands, talon) = DeckUtils.dealCards(deck, players)

        // Each player gets 10 cards
        assertEquals(10, hands["p1"]?.size)
        assertEquals(10, hands["p2"]?.size)
        assertEquals(10, hands["p3"]?.size)

        // Talon gets 2 cards
        assertEquals(2, talon.size)

        // Total is 32 cards
        val totalCards = hands.values.flatten().size + talon.size
        assertEquals(32, totalCards)
    }

    @Test
    fun `all cards are dealt without duplicates`() {
        val deck = Card.createDeck()
        val players = listOf("p1", "p2", "p3")

        val (hands, talon) = DeckUtils.dealCards(deck, players)

        val allDealtCards = hands.values.flatten() + talon
        assertEquals(32, allDealtCards.size)
        assertEquals(32, allDealtCards.toSet().size) // No duplicates
    }

    @Test
    fun `deal preserves card order from deck`() {
        val deck = Card.createDeck()
        val players = listOf("p1", "p2", "p3")

        val (hands, talon) = DeckUtils.dealCards(deck, players)

        // First 7 cards go to p1
        assertEquals(deck.subList(0, 7), hands["p1"]?.subList(0, 7))

        // Cards 7-14 go to p2
        assertEquals(deck.subList(7, 14), hands["p2"]?.subList(0, 7))

        // Cards 14-21 go to p3
        assertEquals(deck.subList(14, 21), hands["p3"]?.subList(0, 7))

        // Cards 21-23 are talon
        assertEquals(deck.subList(21, 23), talon)
    }
}
