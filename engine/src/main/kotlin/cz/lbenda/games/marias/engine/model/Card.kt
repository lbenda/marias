package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val suit: Suit,
    val rank: Rank
) : Comparable<Card> {

    override fun compareTo(other: Card): Int {
        val suitComparison = suit.ordinal.compareTo(other.suit.ordinal)
        return if (suitComparison != 0) suitComparison else rank.ordinal.compareTo(other.rank.ordinal)
    }

    override fun toString(): String = "${rank.symbol}${suit.symbol}"

    fun toFullString(): String = "${rank.name} of ${suit.name}"

    companion object {
        fun createDeck(deckType: DeckType = DeckType.STANDARD_52): List<Card> = deckType.createDeck()

        fun createShuffledDeck(deckType: DeckType = DeckType.STANDARD_52): List<Card> = deckType.createShuffledDeck()

        // Convenience methods for common deck types
        fun createStandard52Deck(): List<Card> = DeckType.STANDARD_52.createDeck()
        fun createPiquet32Deck(): List<Card> = DeckType.PIQUET_32.createDeck()
    }
}
