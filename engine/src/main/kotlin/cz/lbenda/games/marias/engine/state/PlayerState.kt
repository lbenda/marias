package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
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

    val allWonCards: List<Card> get() = wonTricks.flatten()

    fun hasCard(card: Card): Boolean = hand.contains(card)

    fun hasCardsOfSuit(suit: Suit): Boolean = hand.any { it.suit == suit }
}
