package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Suit(
    val czechName: String,
    val germanName: String,
    val symbol: String
) {
    ZELENE("Zelené", "Laub", "♠"),
    ZALUDY("Žaludy", "Eichel", "♣"),
    KULE("Kule", "Schellen", "♦"),
    SRDCE("Srdce", "Herz", "♥");

    companion object {
        fun fromCzechName(name: String): Suit? = entries.find { it.czechName.equals(name, ignoreCase = true) }
    }
}
