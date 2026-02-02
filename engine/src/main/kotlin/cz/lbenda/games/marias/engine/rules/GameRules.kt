package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType

class GameRules {

    fun validateAction(state: GameState, action: GameAction): String? {
        return when (action) {
            is GameAction.JoinGame -> validateJoinGame(state, action)
            is GameAction.LeaveGame -> validateLeaveGame(state, action)
            is GameAction.StartGame -> validateStartGame(state, action)
            is GameAction.DealCards -> validateDealCards(state, action)
            is GameAction.PlaceBid -> validatePlaceBid(state, action)
            is GameAction.Pass -> validatePass(state, action)
            is GameAction.ExchangeTalon -> validateExchangeTalon(state, action)
            is GameAction.SelectTrump -> validateSelectTrump(state, action)
            is GameAction.PlayCard -> validatePlayCard(state, action)
            is GameAction.DeclareMarriage -> validateDeclareMarriage(state, action)
            is GameAction.StartNewRound -> validateStartNewRound(state, action)
        }
    }

    private fun validateJoinGame(state: GameState, action: GameAction.JoinGame): String? {
        if (state.phase != GamePhase.WAITING_FOR_PLAYERS) {
            return "Cannot join game: game has already started"
        }
        if (state.players.size >= 3) {
            return "Cannot join game: game is full"
        }
        if (state.players.containsKey(action.playerId)) {
            return "Cannot join game: player already in game"
        }
        if (action.playerName.isBlank()) {
            return "Cannot join game: player name cannot be empty"
        }
        return null
    }

    private fun validateLeaveGame(state: GameState, action: GameAction.LeaveGame): String? {
        if (!state.players.containsKey(action.playerId)) {
            return "Cannot leave game: player not in game"
        }
        return null
    }

    private fun validateStartGame(state: GameState, action: GameAction.StartGame): String? {
        if (state.phase != GamePhase.WAITING_FOR_PLAYERS) {
            return "Cannot start game: game has already started"
        }
        if (state.players.size != 3) {
            return "Cannot start game: need exactly 3 players"
        }
        return null
    }

    private fun validateDealCards(state: GameState, action: GameAction.DealCards): String? {
        if (state.phase != GamePhase.DEALING) {
            return "Cannot deal cards: not in dealing phase"
        }
        return null
    }

    private fun validatePlaceBid(state: GameState, action: GameAction.PlaceBid): String? {
        if (state.phase != GamePhase.BIDDING) {
            return "Cannot place bid: not in bidding phase"
        }
        if (state.currentPlayerId != action.playerId) {
            return "Cannot place bid: not your turn"
        }
        if (action.playerId in state.biddingState.passedPlayers) {
            return "Cannot place bid: you have already passed"
        }
        if (!state.biddingState.canBid(action.gameType)) {
            return "Cannot place bid: bid must be higher than current bid"
        }
        return null
    }

    private fun validatePass(state: GameState, action: GameAction.Pass): String? {
        if (state.phase != GamePhase.BIDDING) {
            return "Cannot pass: not in bidding phase"
        }
        if (state.currentPlayerId != action.playerId) {
            return "Cannot pass: not your turn"
        }
        if (action.playerId in state.biddingState.passedPlayers) {
            return "Cannot pass: you have already passed"
        }
        return null
    }

    private fun validateExchangeTalon(state: GameState, action: GameAction.ExchangeTalon): String? {
        if (state.phase != GamePhase.TALON_EXCHANGE) {
            return "Cannot exchange talon: not in talon exchange phase"
        }
        if (action.playerId != state.declarerPlayerId) {
            return "Cannot exchange talon: only declarer can exchange"
        }
        if (action.cardsToDiscard.size != 2) {
            return "Cannot exchange talon: must discard exactly 2 cards"
        }
        val declarer = state.players[action.playerId]!!
        val allCards = declarer.hand + state.talon
        for (card in action.cardsToDiscard) {
            if (card !in allCards) {
                return "Cannot exchange talon: invalid card to discard"
            }
        }
        return null
    }

    private fun validateSelectTrump(state: GameState, action: GameAction.SelectTrump): String? {
        if (state.phase != GamePhase.TRUMP_SELECTION) {
            return "Cannot select trump: not in trump selection phase"
        }
        if (action.playerId != state.declarerPlayerId) {
            return "Cannot select trump: only declarer can select trump"
        }
        return null
    }

    private fun validatePlayCard(state: GameState, action: GameAction.PlayCard): String? {
        if (state.phase != GamePhase.PLAYING) {
            return "Cannot play card: not in playing phase"
        }
        if (state.currentPlayerId != action.playerId) {
            return "Cannot play card: not your turn"
        }
        val player = state.players[action.playerId]!!
        if (!player.hasCard(action.card)) {
            return "Cannot play card: you don't have this card"
        }

        // Check following suit rules
        val leadSuit = state.currentTrick.leadSuit
        if (leadSuit != null && action.card.suit != leadSuit) {
            if (player.hasCardsOfSuit(leadSuit)) {
                return "Cannot play card: must follow suit"
            }
            // Check if must trump
            if (state.trump != null && action.card.suit != state.trump && player.hasCardsOfSuit(state.trump)) {
                return "Cannot play card: must play trump if you can't follow suit"
            }
        }

        return null
    }

    private fun validateDeclareMarriage(state: GameState, action: GameAction.DeclareMarriage): String? {
        if (state.phase != GamePhase.PLAYING) {
            return "Cannot declare marriage: not in playing phase"
        }
        val player = state.players[action.playerId]!!
        val hasKing = player.hand.any { it.suit == action.suit && it.rank == cz.lbenda.games.marias.engine.model.Rank.KING }
        val hasQueen = player.hand.any { it.suit == action.suit && it.rank == cz.lbenda.games.marias.engine.model.Rank.QUEEN }
        if (!hasKing || !hasQueen) {
            return "Cannot declare marriage: you need both King and Queen of the suit"
        }
        return null
    }

    private fun validateStartNewRound(state: GameState, action: GameAction.StartNewRound): String? {
        if (state.phase != GamePhase.SCORING && state.phase != GamePhase.FINISHED) {
            return "Cannot start new round: current round not finished"
        }
        return null
    }

    fun getValidCards(state: GameState, playerId: String): List<Card> {
        val player = state.players[playerId] ?: return emptyList()
        if (state.phase != GamePhase.PLAYING || state.currentPlayerId != playerId) {
            return emptyList()
        }

        val leadSuit = state.currentTrick.leadSuit ?: return player.hand

        // Must follow suit if possible
        val suitCards = player.hand.filter { it.suit == leadSuit }
        if (suitCards.isNotEmpty()) {
            return suitCards
        }

        // Must trump if possible and can't follow suit
        if (state.trump != null) {
            val trumpCards = player.hand.filter { it.suit == state.trump }
            if (trumpCards.isNotEmpty()) {
                return trumpCards
            }
        }

        // Can play anything
        return player.hand
    }
}
