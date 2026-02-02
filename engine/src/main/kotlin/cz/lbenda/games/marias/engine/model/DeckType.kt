package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class DeckType(
    val suits: List<Suit>,
    val ranks: List<Rank>,
    val description: String
) {
    STANDARD_52(
        suits = Suit.entries.toList(),
        ranks = Rank.entries.toList(),
        description = "Standard 52-card deck"
    ),

    PIQUET_32(
        suits = Suit.entries.toList(),
        ranks = listOf(Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE),
        description = "32-card Piquet deck (7-A), used in Mariáš, Skat, Belote"
    ),

    EUCHRE_24(
        suits = Suit.entries.toList(),
        ranks = listOf(Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE),
        description = "24-card Euchre deck (9-A)"
    ),

    PINOCHLE_48(
        suits = Suit.entries.toList(),
        ranks = listOf(Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE),
        description = "48-card Pinochle deck (9-A, doubled)"
    ),

    JASS_36(
        suits = Suit.entries.toList(),
        ranks = listOf(Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE),
        description = "36-card Jass deck (6-A)"
    );

    val cardCount: Int
        get() = when (this) {
            PINOCHLE_48 -> suits.size * ranks.size * 2 // Doubled deck
            else -> suits.size * ranks.size
        }

    fun createDeck(): List<Card> = when (this) {
        PINOCHLE_48 -> {
            // Pinochle has two of each card
            val singleDeck = suits.flatMap { suit -> ranks.map { rank -> Card(suit, rank) } }
            singleDeck + singleDeck
        }
        else -> suits.flatMap { suit -> ranks.map { rank -> Card(suit, rank) } }
    }

    fun createShuffledDeck(): List<Card> = createDeck().shuffled()
}
