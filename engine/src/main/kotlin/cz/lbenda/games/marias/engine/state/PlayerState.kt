package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val playerId: String,
    val name: String,
    val hand: List<Card> = emptyList(),
    val wonTricks: List<List<Card>> = emptyList(),
    val score: Int = 0,
    val hasPassed: Boolean = false,
    val isDealer: Boolean = false,
    val seatPosition: Int = 0
) {
    val tricksWonCount: Int get() = wonTricks.size

    val pointsInTricks: Int get() = wonTricks.flatten().sumOf { it.pointValue }

    fun hasCard(card: Card): Boolean = hand.contains(card)

    fun hasCardsOfSuit(suit: cz.lbenda.games.marias.engine.model.Suit): Boolean =
        hand.any { it.suit == suit }
}
