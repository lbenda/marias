package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.TrickState

class TrickResolver {

    fun determineTrickWinner(trick: TrickState, trump: Suit?): String {
        require(trick.isComplete) { "Trick must be complete to determine winner" }

        val leadSuit = trick.leadSuit!!
        val cardsPlayed = trick.cardsPlayed

        // Find the highest trump if any trump was played
        if (trump != null) {
            val trumpCards = cardsPlayed.filter { it.card.suit == trump }
            if (trumpCards.isNotEmpty()) {
                return trumpCards.maxByOrNull { MariasCardValues.getStrength(it.card) }!!.playerId
            }
        }

        // Otherwise, highest card of lead suit wins
        val leadSuitCards = cardsPlayed.filter { it.card.suit == leadSuit }
        return leadSuitCards.maxByOrNull { MariasCardValues.getStrength(it.card) }!!.playerId
    }

    fun calculateTrickPoints(trick: TrickState): Int {
        return trick.cardsPlayed.sumOf { MariasCardValues.getPointValue(it.card) }
    }
}
