package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.createDeck
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeckUtilsTest {

    @Test
    fun `deal distributes cards correctly`() {
        val deck = createDeck()
        val (hands, talon) = dealCards(deck, listOf("p1", "p2", "p3"))

        assertEquals(10, hands["p1"]?.size)
        assertEquals(10, hands["p2"]?.size)
        assertEquals(10, hands["p3"]?.size)
        assertEquals(2, talon.size)
    }

    @Test
    fun `all cards dealt without duplicates`() {
        val deck = createDeck()
        val (hands, talon) = dealCards(deck, listOf("p1", "p2", "p3"))
        val all = hands.values.flatten() + talon
        assertEquals(32, all.toSet().size)
    }

    @Test
    fun `deal preserves order`() {
        val deck = createDeck()
        val (hands, talon) = dealCards(deck, listOf("p1", "p2", "p3"))

        assertEquals(deck.subList(0, 7), hands["p1"]?.subList(0, 7))
        assertEquals(deck.subList(7, 14), hands["p2"]?.subList(0, 7))
        assertEquals(deck.subList(21, 23), talon)
    }
}
