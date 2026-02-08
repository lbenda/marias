package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.engine.rules.GameRuleSet
import cz.lbenda.games.engine.state.BaseGameState
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

    override fun possibleActions(state: BaseGameState, playerId: String): List<GameAction> {
        val mState = state as? GameState ?: return emptyList()
        val actions = mutableListOf<GameAction>()

        // Turn-independent actions
        actions.add(GameAction.LeaveGame(playerId))
        mState.players[playerId]?.let { player ->
            actions.add(GameAction.ReorderHand(playerId, player.hand))
        }

        when (mState.phase) {
            GamePhase.WAITING_FOR_PLAYERS -> {
                if (!mState.players.containsKey(playerId)) {
                    // This is a bit tricky since we don't know the name here.
                    // But for the sake of possibleActions, we can say JoinGame is possible.
                }
                if (mState.players.size == 3 && playerId == mState.playerOrder.firstOrNull()) {
                    actions.add(GameAction.StartGame(playerId))
                }
            }
            GamePhase.DEALING -> {
                if (mState.dealing.phase == DealingPhase.NOT_STARTED) {
                    // Usually the dealer or system deals. Assuming dealer for now.
                    if (mState.playerOrder[mState.dealerIndex] == playerId) {
                        actions.add(GameAction.DealCards(playerId))
                    }
                } else if (mState.dealing.decisionGate != null && mState.dealing.decisionGate.playerId == playerId) {
                    if (mState.dealing.canMakeDecision(ChooserDecisionType.SELECT_TRUMP)) {
                        mState.players[playerId]?.hand?.forEach { card ->
                            actions.add(GameAction.ChooseTrump(playerId, card))
                        }
                    }
                    if (mState.dealing.canMakeDecision(ChooserDecisionType.PASS)) {
                        actions.add(GameAction.ChooserPass(playerId))
                    }
                }
            }
            GamePhase.BIDDING -> {
                if (mState.playerOrder.getOrNull(mState.currentPlayerIndex) == playerId) {
                    if (playerId !in mState.bidding.passedPlayers) {
                        actions.add(GameAction.Pass(playerId))
                        GameType.entries.forEach { gameType ->
                            if (mState.bidding.currentBid == null || gameType.ordinal > mState.bidding.currentBid.ordinal) {
                                actions.add(GameAction.PlaceBid(playerId, gameType))
                            }
                        }
                    }
                }
            }
            GamePhase.TALON_EXCHANGE -> {
                if (mState.declarerId == playerId) {
                    val hand = mState.players[playerId]?.hand ?: emptyList()
                    // This can be a LOT of combinations (Hand size is 12, pick 2)
                    // For now, let's just indicate it's possible if we have enough cards.
                    // In a real UI, the client would pick 2 cards.
                }
            }
            GamePhase.TRUMP_SELECTION -> {
                if (mState.declarerId == playerId) {
                    Suit.entries.forEach { suit ->
                        if (canAnnounceSevenVariant(mState.gameType, suit, mState.talon)) {
                            actions.add(GameAction.SelectTrump(playerId, suit))
                        }
                    }
                }
            }
            GamePhase.PLAYING -> {
                if (mState.playerOrder.getOrNull(mState.currentPlayerIndex) == playerId) {
                    validCards(mState, playerId).forEach { card ->
                        actions.add(GameAction.PlayCard(playerId, card))
                    }
                    // Marriage declaration
                    val hand = mState.players[playerId]?.hand ?: emptyList()
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

    override fun reduce(state: BaseGameState, action: GameAction): BaseGameState {
        val mState = state as? GameState ?: return state
        // Delegate to existing global reduce function
        return reduce(mState, action)
    }

    override fun validate(state: BaseGameState, action: GameAction): Boolean {
        val mState = state as? GameState ?: return false
        // Delegate to existing global validate function
        return cz.lbenda.games.marias.engine.rules.validate(mState, action) == null
    }
}
