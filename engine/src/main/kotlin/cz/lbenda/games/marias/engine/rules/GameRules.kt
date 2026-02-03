package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.state.DealingPhase
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState

fun validate(state: GameState, action: GameAction): String? = when (action) {
    is GameAction.JoinGame -> when {
        state.phase != GamePhase.WAITING_FOR_PLAYERS -> "Game already started"
        state.players.size >= 3 -> "Game full"
        state.players.containsKey(action.playerId) -> "Already joined"
        action.playerName.isBlank() -> "Name required"
        else -> null
    }
    is GameAction.LeaveGame -> if (action.playerId !in state.players) "Not in game" else null
    is GameAction.StartGame -> when {
        state.phase != GamePhase.WAITING_FOR_PLAYERS -> "Already started"
        state.players.size != 3 -> "Need 3 players"
        else -> null
    }
    is GameAction.DealCards -> when {
        state.phase != GamePhase.DEALING -> "Not dealing phase"
        state.dealing.phase != DealingPhase.NOT_STARTED -> "Already dealing"
        else -> null
    }
    is GameAction.ChooseTrump -> when {
        state.phase != GamePhase.DEALING -> "Not dealing phase"
        state.dealing.phase != DealingPhase.WAITING_FOR_TRUMP -> "Not waiting for trump selection"
        action.playerId != state.dealing.chooserId -> "Not chooser"
        else -> null
    }
    is GameAction.ChooserPass -> when {
        state.phase != GamePhase.DEALING -> "Not dealing phase"
        state.dealing.phase != DealingPhase.WAITING_FOR_TRUMP -> "Not waiting for trump selection"
        action.playerId != state.dealing.chooserId -> "Not chooser"
        else -> null
    }
    is GameAction.PlaceBid -> when {
        state.phase != GamePhase.BIDDING -> "Not bidding phase"
        state.playerOrder[state.currentPlayerIndex] != action.playerId -> "Not your turn"
        action.playerId in state.bidding.passedPlayers -> "Already passed"
        state.bidding.currentBid != null && action.gameType.ordinal <= state.bidding.currentBid.ordinal -> "Bid too low"
        else -> null
    }
    is GameAction.Pass -> when {
        state.phase != GamePhase.BIDDING -> "Not bidding phase"
        state.playerOrder[state.currentPlayerIndex] != action.playerId -> "Not your turn"
        action.playerId in state.bidding.passedPlayers -> "Already passed"
        else -> null
    }
    is GameAction.ExchangeTalon -> when {
        state.phase != GamePhase.TALON_EXCHANGE -> "Not exchange phase"
        action.playerId != state.declarerId -> "Not declarer"
        action.cardsToDiscard.size != 2 -> "Must discard 2 cards"
        else -> null
    }
    is GameAction.SelectTrump -> when {
        state.phase != GamePhase.TRUMP_SELECTION -> "Not trump selection phase"
        action.playerId != state.declarerId -> "Not declarer"
        else -> null
    }
    is GameAction.PlayCard -> validatePlayCard(state, action)
    is GameAction.DeclareMarriage -> when {
        state.phase != GamePhase.PLAYING -> "Not playing phase"
        !hasMarriage(state.players[action.playerId]!!.hand, action.suit) -> "No marriage"
        else -> null
    }
    is GameAction.StartNewRound -> when {
        state.phase != GamePhase.SCORING && state.phase != GamePhase.FINISHED -> "Round not finished"
        else -> null
    }
}

private fun validatePlayCard(state: GameState, action: GameAction.PlayCard): String? {
    if (state.phase != GamePhase.PLAYING) return "Not playing phase"
    if (state.playerOrder[state.currentPlayerIndex] != action.playerId) return "Not your turn"

    val hand = state.players[action.playerId]!!.hand
    if (action.card !in hand) return "Card not in hand"

    val leadSuit = state.trick.leadSuit
    if (leadSuit != null && action.card.suit != leadSuit) {
        if (hand.any { it.suit == leadSuit }) return "Must follow suit"
        if (state.trump != null && action.card.suit != state.trump && hand.any { it.suit == state.trump }) {
            return "Must trump"
        }
    }
    return null
}

fun validCards(state: GameState, playerId: String): List<Card> {
    if (state.phase != GamePhase.PLAYING) return emptyList()
    if (state.playerOrder[state.currentPlayerIndex] != playerId) return emptyList()

    val hand = state.players[playerId]!!.hand
    val leadSuit = state.trick.leadSuit ?: return hand

    val suitCards = hand.filter { it.suit == leadSuit }
    if (suitCards.isNotEmpty()) return suitCards

    if (state.trump != null) {
        val trumpCards = hand.filter { it.suit == state.trump }
        if (trumpCards.isNotEmpty()) return trumpCards
    }

    return hand
}
