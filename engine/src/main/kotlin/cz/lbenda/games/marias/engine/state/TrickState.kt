package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import kotlinx.serialization.Serializable

@Serializable
data class TrickState(
    val cardsPlayed: List<PlayedCard> = emptyList(),
    val leadPlayerId: String? = null,
    val trickNumber: Int = 0
) {
    val leadSuit: Suit? get() = cardsPlayed.firstOrNull()?.card?.suit

    val isComplete: Boolean get() = cardsPlayed.size == 3

    val isEmpty: Boolean get() = cardsPlayed.isEmpty()
}

@Serializable
data class PlayedCard(
    val playerId: String,
    val card: Card
)
