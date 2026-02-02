package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Rank(
    val czechName: String,
    val pointValue: Int,
    val strength: Int
) {
    SEDMICKA("Sedmička", 0, 1),
    OSMICKA("Osmička", 0, 2),
    DEVITKA("Devítka", 0, 3),
    DESITKA("Desítka", 10, 4),
    SPODEK("Spodek", 2, 5),
    SVRSEK("Svršek", 3, 6),
    KRAL("Král", 4, 7),
    ESO("Eso", 11, 8);

    companion object {
        fun fromCzechName(name: String): Rank? = entries.find { it.czechName.equals(name, ignoreCase = true) }
    }
}
