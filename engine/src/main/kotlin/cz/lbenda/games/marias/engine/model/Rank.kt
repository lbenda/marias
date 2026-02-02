package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Rank(val symbol: String, val numericValue: Int) {
    ACE("A", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13);

    companion object {
        val STANDARD_ORDER = entries.toList()
        val ACE_HIGH_ORDER = listOf(TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE)
    }
}
