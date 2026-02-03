package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.createShuffledDeck
import cz.lbenda.games.marias.engine.rules.dealCards
import cz.lbenda.games.marias.engine.rules.determineTrickWinner
import cz.lbenda.games.marias.engine.rules.validate
import cz.lbenda.games.marias.engine.state.*

fun reduce(state: GameState, action: GameAction): GameState {
    validate(state, action)?.let { return state.copy(error = it, version = state.version + 1) }

    val next = when (action) {
        is GameAction.JoinGame -> state.copy(
            players = state.players + (action.playerId to PlayerState(action.playerId, action.playerName)),
            playerOrder = state.playerOrder + action.playerId
        )
        is GameAction.LeaveGame -> state.copy(
            players = state.players - action.playerId,
            playerOrder = state.playerOrder - action.playerId,
            phase = if (state.players.size == 1) GamePhase.FINISHED else state.phase
        )
        is GameAction.StartGame -> state.copy(phase = GamePhase.DEALING)
        is GameAction.DealCards -> dealCardsReducer(state, action)
        is GameAction.PlaceBid -> placeBidReducer(state, action)
        is GameAction.Pass -> passReducer(state, action)
        is GameAction.ExchangeTalon -> exchangeTalonReducer(state, action)
        is GameAction.SelectTrump -> state.copy(
            trump = action.trump,
            phase = GamePhase.PLAYING,
            currentPlayerIndex = (state.dealerIndex + 1) % 3,
            trick = TrickState(leadPlayerId = state.playerOrder[(state.dealerIndex + 1) % 3])
        )
        is GameAction.PlayCard -> playCardReducer(state, action)
        is GameAction.DeclareMarriage -> state // TODO: track marriages
        is GameAction.StartNewRound -> state.copy(
            players = state.players.mapValues { it.value.copy(hand = emptyList(), wonCards = emptyList(), hasPassed = false, isDealer = false) },
            dealerIndex = (state.dealerIndex + 1) % 3,
            currentPlayerIndex = (state.dealerIndex + 2) % 3,
            talon = emptyList(),
            trump = null,
            gameType = null,
            declarerId = null,
            bidding = BiddingState(),
            trick = TrickState(),
            tricksPlayed = 0,
            phase = GamePhase.DEALING,
            roundNumber = state.roundNumber + 1
        )
    }
    return next.copy(error = null, version = state.version + 1)
}

private fun dealCardsReducer(state: GameState, action: GameAction.DealCards): GameState {
    val deck = action.deck ?: createShuffledDeck()
    val (hands, talon) = dealCards(deck, state.playerOrder)

    return state.copy(
        players = state.players.mapValues { (id, p) ->
            p.copy(hand = hands[id]!!.sorted(), isDealer = state.playerOrder.indexOf(id) == state.dealerIndex)
        },
        talon = talon,
        phase = GamePhase.BIDDING,
        currentPlayerIndex = (state.dealerIndex + 1) % 3
    )
}

private fun placeBidReducer(state: GameState, action: GameAction.PlaceBid): GameState {
    val nextIndex = nextActiveBidder(state.playerOrder, state.currentPlayerIndex, state.bidding.passedPlayers)
    return state.copy(
        bidding = state.bidding.copy(currentBid = action.gameType, bidderId = action.playerId),
        currentPlayerIndex = nextIndex
    )
}

private fun passReducer(state: GameState, action: GameAction.Pass): GameState {
    val newPassed = state.bidding.passedPlayers + action.playerId
    val active = state.playerOrder.filter { it !in newPassed }

    // Bidding ends when only one active player remains
    if (active.size == 1) {
        val declarer = if (state.bidding.currentBid != null) active.first() else state.playerOrder[state.dealerIndex]
        val gameType = state.bidding.currentBid ?: GameType.HRA
        return state.copy(
            bidding = state.bidding.copy(passedPlayers = newPassed),
            declarerId = declarer,
            gameType = gameType,
            phase = GamePhase.TALON_EXCHANGE,
            currentPlayerIndex = state.playerOrder.indexOf(declarer)
        )
    }

    return state.copy(
        bidding = state.bidding.copy(passedPlayers = newPassed),
        currentPlayerIndex = nextActiveBidder(state.playerOrder, state.currentPlayerIndex, newPassed)
    )
}

private fun nextActiveBidder(order: List<String>, current: Int, passed: Set<String>): Int {
    var next = (current + 1) % order.size
    repeat(order.size) {
        if (order[next] !in passed) return next
        next = (next + 1) % order.size
    }
    return current
}

private fun exchangeTalonReducer(state: GameState, action: GameAction.ExchangeTalon): GameState {
    val declarer = state.players[action.playerId]!!
    val newHand = (declarer.hand + state.talon).filterNot { it in action.cardsToDiscard }.sorted()
    val nextPhase = if (state.gameType?.requiresTrump == true) GamePhase.TRUMP_SELECTION else GamePhase.PLAYING

    return state.copy(
        players = state.players + (action.playerId to declarer.copy(hand = newHand)),
        talon = action.cardsToDiscard,
        phase = nextPhase
    )
}

private fun playCardReducer(state: GameState, action: GameAction.PlayCard): GameState {
    val player = state.players[action.playerId]!!
    val newHand = player.hand - action.card
    val newTrick = state.trick.copy(
        cards = state.trick.cards + (action.playerId to action.card),
        leadPlayerId = state.trick.leadPlayerId ?: action.playerId
    )

    var newPlayers = state.players + (action.playerId to player.copy(hand = newHand))
    var trick = newTrick
    var tricksPlayed = state.tricksPlayed
    var nextPlayer = (state.currentPlayerIndex + 1) % 3
    var phase = state.phase

    // Trick complete
    if (newTrick.isComplete) {
        val winnerId = determineTrickWinner(newTrick, state.trump)
        val winner = newPlayers[winnerId]!!
        newPlayers = newPlayers + (winnerId to winner.copy(wonCards = winner.wonCards + newTrick.cards.map { it.second }))
        tricksPlayed++

        if (tricksPlayed == 10) {
            phase = GamePhase.SCORING
            trick = TrickState()
        } else {
            trick = TrickState(leadPlayerId = winnerId)
            nextPlayer = state.playerOrder.indexOf(winnerId)
        }
    }

    return state.copy(
        players = newPlayers,
        trick = trick,
        tricksPlayed = tricksPlayed,
        phase = phase,
        currentPlayerIndex = nextPlayer
    )
}
