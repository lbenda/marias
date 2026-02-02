package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val suit: Suit,
    val rank: Rank
) : Comparable<Card> {

    val pointValue: Int get() = rank.pointValue

    val strength: Int get() = rank.strength

    override fun compareTo(other: Card): Int {
        val suitComparison = suit.ordinal.compareTo(other.suit.ordinal)
        return if (suitComparison != 0) suitComparison else strength.compareTo(other.strength)
    }

    override fun toString(): String = "${rank.czechName} ${suit.czechName}"

    fun toShortString(): String = "${rank.czechName.take(2)}${suit.symbol}"

    companion object {
        fun createDeck(): List<Card> = Suit.entries.flatMap { suit ->
            Rank.entries.map { rank -> Card(suit, rank) }
        }

        fun createShuffledDeck(): List<Card> = createDeck().shuffled()
    }
}
