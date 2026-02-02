package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Suit(val symbol: String) {
    SPADES("♠"),
    CLUBS("♣"),
    DIAMONDS("♦"),
    HEARTS("♥");

    companion object {
        val FRENCH_ORDER = listOf(SPADES, CLUBS, DIAMONDS, HEARTS)
        val GERMAN_ORDER = listOf(SPADES, CLUBS, DIAMONDS, HEARTS) // Laub, Eichel, Schellen, Herz
    }
}
