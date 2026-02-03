package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(val suit: Suit, val rank: Rank) : Comparable<Card> {
    val points: Int get() = rank.points
    val strength: Int get() = rank.strength
    override fun toString(): String = "${rank.symbol}${suit.symbol}"
    override fun compareTo(other: Card): Int {
        val s = suit.ordinal.compareTo(other.suit.ordinal)
        return if (s != 0) s else rank.ordinal.compareTo(other.rank.ordinal)
    }
}

fun createDeck(): List<Card> = Suit.entries.flatMap { suit ->
    Rank.entries.map { rank -> Card(suit, rank) }
}

fun createShuffledDeck(): List<Card> = createDeck().shuffled()
