package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

@Serializable
enum class GameType(
    val czechName: String,
    val baseValue: Int,
    val requiresTrump: Boolean
) {
    HRA("Hra", 1, true),
    SEDMA("Sedma", 2, true),
    KILO("Kilo", 4, true),
    BETL("Betl", 5, false),
    DURCH("Durch", 6, false);

    companion object {
        fun fromCzechName(name: String): GameType? = entries.find { it.czechName.equals(name, ignoreCase = true) }
    }
}
