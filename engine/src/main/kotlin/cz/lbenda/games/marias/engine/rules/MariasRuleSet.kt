package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.engine.rules.GameRuleSet
import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.reducer.reduce
import cz.lbenda.games.marias.engine.rules.canAnnounceSevenVariant
import cz.lbenda.games.marias.engine.rules.hasMarriage
import cz.lbenda.games.marias.engine.rules.validCards
import cz.lbenda.games.marias.engine.state.ChooserDecisionType
import cz.lbenda.games.marias.engine.state.DealingPhase
import cz.lbenda.games.marias.engine.state.GamePhase
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType

/**
 * Implementation of Mariash rules as a [GameRuleSet].
 */
class MariasRuleSet : GameRuleSet {

    override fun possibleActions(state: GameState, playerId: String): List<GameAction> {
        val actions = mutableListOf<GameAction>()

        // Turn-independent actions
        actions.add(GameAction.LeaveGame(playerId))
        state.players[playerId]?.let { player ->
            actions.add(GameAction.ReorderHand(playerId, player.hand))
        }

        when (state.phase) {
            GamePhase.WAITING_FOR_PLAYERS -> {
                if (!state.players.containsKey(playerId)) {
                    // This is a bit tricky since we don't know the name here.
                    // But for the sake of possibleActions, we can say JoinGame is possible.
                }
                if (state.players.size == 3 && playerId == state.playerOrder.firstOrNull()) {
                    actions.add(GameAction.StartGame(playerId))
                }
            }
            GamePhase.DEALING -> {
                if (state.dealing.phase == DealingPhase.NOT_STARTED) {
                    // Usually the dealer or system deals. Assuming dealer for now.
                    if (state.playerOrder[state.dealerIndex] == playerId) {
                        actions.add(GameAction.DealCards(playerId))
                    }
                } else if (state.dealing.decisionGate != null && state.dealing.decisionGate.playerId == playerId) {
                    if (state.dealing.canMakeDecision(ChooserDecisionType.SELECT_TRUMP)) {
                        state.players[playerId]?.hand?.forEach { card ->
                            actions.add(GameAction.ChooseTrump(playerId, card))
                        }
                    }
                    if (state.dealing.canMakeDecision(ChooserDecisionType.PASS)) {
                        actions.add(GameAction.ChooserPass(playerId))
                    }
                }
            }
            GamePhase.BIDDING -> {
                if (state.playerOrder.getOrNull(state.currentPlayerIndex) == playerId) {
                    if (playerId !in state.bidding.passedPlayers) {
                        actions.add(GameAction.Pass(playerId))
                        GameType.entries.forEach { gameType ->
                            if (state.bidding.currentBid == null || gameType.ordinal > state.bidding.currentBid.ordinal) {
                                actions.add(GameAction.PlaceBid(playerId, gameType))
                            }
                        }
                    }
                }
            }
            GamePhase.TALON_EXCHANGE -> {
                if (state.declarerId == playerId) {
                    val hand = state.players[playerId]?.hand ?: emptyList()
                    // This can be a LOT of combinations (Hand size is 12, pick 2)
                    // For now, let's just indicate it's possible if we have enough cards.
                    // In a real UI, the client would pick 2 cards.
                }
            }
            GamePhase.TRUMP_SELECTION -> {
                if (state.declarerId == playerId) {
                    Suit.entries.forEach { suit ->
                        if (canAnnounceSevenVariant(state.gameType, suit, state.talon)) {
                            actions.add(GameAction.SelectTrump(playerId, suit))
                        }
                    }
                }
            }
            GamePhase.PLAYING -> {
                if (state.playerOrder.getOrNull(state.currentPlayerIndex) == playerId) {
                    validCards(state, playerId).forEach { card ->
                        actions.add(GameAction.PlayCard(playerId, card))
                    }
                    // Marriage declaration
                    val hand = state.players[playerId]?.hand ?: emptyList()
                    Suit.entries.forEach { suit ->
                        if (hasMarriage(hand, suit)) {
                            actions.add(GameAction.DeclareMarriage(playerId, suit))
                        }
                    }
                }
            }
            GamePhase.SCORING, GamePhase.FINISHED -> {
                actions.add(GameAction.StartNewRound(playerId))
            }
        }

        return actions
    }

    override fun reduce(state: GameState, action: GameAction): GameState {
        // Delegate to existing global reduce function
        return reduce(state, action)
    }

    override fun validate(state: GameState, action: GameAction): Boolean {
        // Delegate to existing global validate function
        return cz.lbenda.games.marias.engine.rules.validate(state, action) == null
    }
}
