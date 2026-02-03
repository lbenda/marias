package cz.lbenda.games.marias.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Rank(
    val symbol: String,
    val points: Int,      // Mariáš point value
    val strength: Int     // Mariáš trick-taking strength
) {
    SEVEN("7", 0, 1),
    EIGHT("8", 0, 2),
    NINE("9", 0, 3),
    TEN("10", 10, 4),
    JACK("J", 2, 5),
    QUEEN("Q", 3, 6),
    KING("K", 4, 7),
    ACE("A", 11, 8)
}
