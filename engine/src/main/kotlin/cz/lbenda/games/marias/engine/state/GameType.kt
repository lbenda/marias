package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

/**
 * Game contract types in mariash.
 * See docs/VOCABULARY.md for terminology.
 */
@Serializable
enum class GameType(
    val displayName: String,
    val baseValue: Int,
    val requiresTrump: Boolean
) {
    GAME("Game", 1, true),
    SEVEN("Seven", 2, true),
    HUNDRED("Hundred", 4, true),
    HUNDRED_SEVEN("Hundred-Seven", 6, true),
    MISERE("Misere", 5, false),
    SLAM("Slam", 6, false),
    TWO_SEVENS("Two Sevens", 8, true);

    companion object {
        fun fromName(name: String): GameType? = entries.find { it.displayName.equals(name, ignoreCase = true) }
    }
}
