package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import kotlinx.serialization.Serializable

@Serializable
data class TrickState(
    val cards: List<Pair<String, Card>> = emptyList(),  // playerId to card
    val leadPlayerId: String? = null
) {
    val leadSuit get() = cards.firstOrNull()?.second?.suit
    val isComplete get() = cards.size == 3
}
