package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.rules.DeckUtils
import cz.lbenda.games.marias.engine.rules.GameRules
import cz.lbenda.games.marias.engine.rules.TrickResolver
import cz.lbenda.games.marias.engine.state.*

class GameReducer(
    private val rules: GameRules = GameRules(),
    private val trickResolver: TrickResolver = TrickResolver()
) {
    fun reduce(state: GameState, action: GameAction): GameState {
        val validationError = rules.validateAction(state, action)
        if (validationError != null) {
            return state.copy(errorMessage = validationError).withIncrementedVersion()
        }

        val newState = when (action) {
            is GameAction.JoinGame -> handleJoinGame(state, action)
            is GameAction.LeaveGame -> handleLeaveGame(state, action)
            is GameAction.StartGame -> handleStartGame(state, action)
            is GameAction.DealCards -> handleDealCards(state, action)
            is GameAction.PlaceBid -> handlePlaceBid(state, action)
            is GameAction.Pass -> handlePass(state, action)
            is GameAction.ExchangeTalon -> handleExchangeTalon(state, action)
            is GameAction.SelectTrump -> handleSelectTrump(state, action)
            is GameAction.PlayCard -> handlePlayCard(state, action)
            is GameAction.DeclareMarriage -> handleDeclareMarriage(state, action)
            is GameAction.StartNewRound -> handleStartNewRound(state, action)
        }

        return newState.copy(errorMessage = null).withIncrementedVersion()
    }

    private fun handleJoinGame(state: GameState, action: GameAction.JoinGame): GameState {
        val newPlayer = PlayerState(
            playerId = action.playerId,
            name = action.playerName,
            seatPosition = state.players.size
        )
        val newPlayers = state.players + (action.playerId to newPlayer)
        val newPlayerOrder = state.playerOrder + action.playerId

        return state.copy(
            players = newPlayers,
            playerOrder = newPlayerOrder
        )
    }

    private fun handleLeaveGame(state: GameState, action: GameAction.LeaveGame): GameState {
        val newPlayers = state.players - action.playerId
        val newPlayerOrder = state.playerOrder - action.playerId

        return state.copy(
            players = newPlayers,
            playerOrder = newPlayerOrder,
            phase = if (newPlayers.isEmpty()) GamePhase.FINISHED else state.phase
        )
    }

    private fun handleStartGame(state: GameState, action: GameAction.StartGame): GameState {
        return state.copy(phase = GamePhase.DEALING)
    }

    private fun handleDealCards(state: GameState, action: GameAction.DealCards): GameState {
        val deck = action.shuffledDeck ?: Card.createShuffledDeck()
        val (playerHands, talon) = DeckUtils.dealCards(deck, state.playerOrder)

        val newPlayers = state.players.mapValues { (playerId, playerState) ->
            val hand = playerHands[playerId] ?: emptyList()
            val isDealer = state.playerOrder.indexOf(playerId) == state.dealerIndex
            playerState.copy(hand = hand.sorted(), isDealer = isDealer)
        }

        val biddingOrder = buildList {
            for (i in 0 until 3) {
                add(state.playerOrder[(state.dealerIndex + 1 + i) % 3])
            }
        }

        return state.copy(
            players = newPlayers,
            talon = talon,
            phase = GamePhase.BIDDING,
            biddingState = BiddingState(
                biddingOrder = biddingOrder,
                currentBidderIndex = 0
            ),
            currentPlayerIndex = (state.dealerIndex + 1) % 3
        )
    }

    private fun handlePlaceBid(state: GameState, action: GameAction.PlaceBid): GameState {
        val nextBidderIndex = state.biddingState.nextBidderIndex
        val newBiddingState = state.biddingState.copy(
            currentBid = action.gameType,
            currentBidder = action.playerId,
            currentBidderIndex = nextBidderIndex
        )

        // Get the player at the new current bidder index
        val nextPlayerId = state.biddingState.biddingOrder[nextBidderIndex]
        val nextPlayerIndex = state.playerOrder.indexOf(nextPlayerId)

        return state.copy(
            biddingState = newBiddingState,
            currentPlayerIndex = nextPlayerIndex
        )
    }

    private fun handlePass(state: GameState, action: GameAction.Pass): GameState {
        val newPassedPlayers = state.biddingState.passedPlayers + action.playerId
        val biddingOrder = state.biddingState.biddingOrder

        // Find next bidder who hasn't passed (including the one who just passed)
        fun findNextBidderIndex(): Int {
            var next = (state.biddingState.currentBidderIndex + 1) % biddingOrder.size
            var attempts = 0
            while (newPassedPlayers.contains(biddingOrder[next]) && attempts < biddingOrder.size) {
                next = (next + 1) % biddingOrder.size
                attempts++
            }
            return next
        }

        val nextBidderIndex = findNextBidderIndex()
        val newBiddingState = state.biddingState.copy(
            passedPlayers = newPassedPlayers,
            currentBidderIndex = nextBidderIndex
        )

        // Check if bidding is complete (only one active bidder left or all passed)
        val activeBidders = biddingOrder.filter { it !in newPassedPlayers }

        if (activeBidders.size == 1 && newBiddingState.currentBid != null) {
            // Bidding complete, one winner
            val declarer = activeBidders.first()
            val declarerIndex = state.playerOrder.indexOf(declarer)

            return state.copy(
                biddingState = newBiddingState,
                declarerPlayerId = declarer,
                gameType = newBiddingState.currentBid,
                phase = GamePhase.TALON_EXCHANGE,
                currentPlayerIndex = declarerIndex
            )
        } else if (activeBidders.isEmpty() || (activeBidders.size == 1 && newBiddingState.currentBid == null)) {
            // All passed without a bid - dealer must play "Hra"
            val dealer = state.playerOrder[state.dealerIndex]

            return state.copy(
                biddingState = newBiddingState,
                declarerPlayerId = dealer,
                gameType = GameType.HRA,
                phase = GamePhase.TALON_EXCHANGE,
                currentPlayerIndex = state.dealerIndex
            )
        }

        val nextPlayerId = biddingOrder[nextBidderIndex]
        val nextPlayerIndex = state.playerOrder.indexOf(nextPlayerId)

        return state.copy(
            biddingState = newBiddingState,
            currentPlayerIndex = nextPlayerIndex
        )
    }

    private fun handleExchangeTalon(state: GameState, action: GameAction.ExchangeTalon): GameState {
        val declarer = state.players[action.playerId] ?: return state

        // Add talon to declarer's hand, then remove discarded cards
        val newHand = (declarer.hand + state.talon)
            .filterNot { it in action.cardsToDiscard }
            .sorted()

        val newPlayers = state.players + (action.playerId to declarer.copy(hand = newHand))

        val nextPhase = if (state.gameType?.requiresTrump == true) {
            GamePhase.TRUMP_SELECTION
        } else {
            GamePhase.PLAYING
        }

        return state.copy(
            players = newPlayers,
            talon = action.cardsToDiscard, // Store discarded cards as new talon (won by declarer at end)
            phase = nextPhase
        )
    }

    private fun handleSelectTrump(state: GameState, action: GameAction.SelectTrump): GameState {
        // First player after dealer leads
        val leadPlayerIndex = (state.dealerIndex + 1) % 3

        return state.copy(
            trump = action.trump,
            phase = GamePhase.PLAYING,
            currentPlayerIndex = leadPlayerIndex,
            currentTrick = TrickState(
                leadPlayerId = state.playerOrder[leadPlayerIndex],
                trickNumber = 1
            )
        )
    }

    private fun handlePlayCard(state: GameState, action: GameAction.PlayCard): GameState {
        val player = state.players[action.playerId] ?: return state

        // Remove card from player's hand
        val newHand = player.hand.filterNot { it == action.card }
        val updatedPlayer = player.copy(hand = newHand)

        // Add card to current trick
        val playedCard = PlayedCard(action.playerId, action.card)
        val newTrick = state.currentTrick.copy(
            cardsPlayed = state.currentTrick.cardsPlayed + playedCard,
            leadPlayerId = state.currentTrick.leadPlayerId ?: action.playerId
        )

        var newPlayers = state.players + (action.playerId to updatedPlayer)
        var newCurrentTrick = newTrick
        var newCompletedTricks = state.completedTricks
        var newPhase = state.phase
        var newCurrentPlayerIndex = state.nextPlayerIndex()

        // Check if trick is complete
        if (newTrick.isComplete) {
            val winnerId = trickResolver.determineTrickWinner(newTrick, state.trump)
            val winnerIndex = state.playerOrder.indexOf(winnerId)

            // Award trick to winner
            val winner = newPlayers[winnerId]!!
            val wonCards = newTrick.cardsPlayed.map { it.card }
            val updatedWinner = winner.copy(wonTricks = winner.wonTricks + listOf(wonCards))
            newPlayers = newPlayers + (winnerId to updatedWinner)

            newCompletedTricks = newCompletedTricks + newTrick

            // Check if round is complete (10 tricks played)
            if (newCompletedTricks.size == 10) {
                newPhase = GamePhase.SCORING
                newCurrentTrick = TrickState()
            } else {
                // Start new trick with winner leading
                newCurrentTrick = TrickState(
                    leadPlayerId = winnerId,
                    trickNumber = newCompletedTricks.size + 1
                )
                newCurrentPlayerIndex = winnerIndex
            }
        }

        return state.copy(
            players = newPlayers,
            currentTrick = newCurrentTrick,
            completedTricks = newCompletedTricks,
            phase = newPhase,
            currentPlayerIndex = newCurrentPlayerIndex
        )
    }

    private fun handleDeclareMarriage(state: GameState, action: GameAction.DeclareMarriage): GameState {
        // Marriage declaration adds points - handled in scoring
        // For now, just record it (could add to player state if needed)
        return state
    }

    private fun handleStartNewRound(state: GameState, action: GameAction.StartNewRound): GameState {
        // Reset for new round, rotate dealer
        val newDealerIndex = (state.dealerIndex + 1) % 3

        val resetPlayers = state.players.mapValues { (_, playerState) ->
            playerState.copy(
                hand = emptyList(),
                wonTricks = emptyList(),
                hasPassed = false,
                isDealer = false
            )
        }

        return state.copy(
            players = resetPlayers,
            dealerIndex = newDealerIndex,
            currentPlayerIndex = newDealerIndex,
            talon = emptyList(),
            trump = null,
            gameType = null,
            declarerPlayerId = null,
            biddingState = BiddingState(),
            currentTrick = TrickState(),
            completedTricks = emptyList(),
            phase = GamePhase.DEALING,
            roundNumber = state.roundNumber + 1
        )
    }
}
