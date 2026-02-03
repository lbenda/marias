package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.TrickState

fun determineTrickWinner(trick: TrickState, trump: Suit?): String {
    val dominated = trick.cards
    val leadSuit = trick.leadSuit!!

    // Trump wins if played
    if (trump != null) {
        val trumpCards = dominated.filter { it.second.suit == trump }
        if (trumpCards.isNotEmpty()) {
            return trumpCards.maxBy { it.second.strength }.first
        }
    }

    // Otherwise highest of lead suit wins
    return dominated.filter { it.second.suit == leadSuit }.maxBy { it.second.strength }.first
}

fun trickPoints(trick: TrickState): Int = trick.cards.sumOf { it.second.points }
