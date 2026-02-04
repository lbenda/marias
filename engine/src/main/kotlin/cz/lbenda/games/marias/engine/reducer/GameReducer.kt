package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.createShuffledDeck
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
        is GameAction.ChooseTrump -> chooseTrumpReducer(state, action)
        is GameAction.ChooserPass -> chooserPassReducer(state, action)
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
            trumpCard = null,
            gameType = null,
            declarerId = null,
            dealing = DealingState(),
            bidding = BiddingState(),
            trick = TrickState(),
            tricksPlayed = 0,
            phase = GamePhase.DEALING,
            roundNumber = state.roundNumber + 1
        )
        is GameAction.ReorderHand -> {
            val player = state.players[action.playerId]!!
            state.copy(
                players = state.players + (action.playerId to player.copy(hand = action.cards))
            )
        }
    }
    return next.copy(error = null, version = state.version + 1)
}

private fun dealCardsReducer(state: GameState, action: GameAction.DealCards): GameState {
    val deck = action.deck ?: createShuffledDeck()
    val pattern = action.pattern ?: if (action.twoPhase) DealPattern.TWO_PHASE else DealPattern.STANDARD
    val chooserId = state.playerOrder[(state.dealerIndex + 1) % 3]

    return executeDealingPhaseA(state, deck, pattern, chooserId, action.twoPhase)
}

/**
 * Execute dealing: deal all cards, but for two-phase mode, keep chooser's remaining
 * cards as pending (on table) until the chooser makes a decision.
 */
private fun executeDealingPhaseA(
    state: GameState,
    deck: List<Card>,
    pattern: DealPattern,
    chooserId: String,
    twoPhase: Boolean
): GameState {
    val hands = state.playerOrder.associateWith { mutableListOf<Card>() }
    val dealOrder = state.playerOrder.associateWith { mutableListOf<Card>() }
    val talon = mutableListOf<Card>()
    val pendingCards = mutableListOf<Card>()

    var deckPos = 0
    var chooserCardCount = 0

    // Execute all dealing steps
    for (step in pattern.steps) {
        val targetId = when (step.playerOffset) {
            -1 -> null // talon
            else -> state.playerOrder[(state.dealerIndex + step.playerOffset) % 3]
        }

        repeat(step.cardCount) {
            val card = deck[deckPos++]
            when {
                targetId == null -> talon.add(card)
                twoPhase && targetId == chooserId -> {
                    dealOrder[targetId]!!.add(card)
                    if (chooserCardCount < pattern.previewCardsForChooser) {
                        // First N cards go to hand
                        hands[targetId]!!.add(card)
                        chooserCardCount++
                    } else {
                        // Remaining cards go to pending (on table)
                        pendingCards.add(card)
                    }
                }
                else -> {
                    hands[targetId]!!.add(card)
                    dealOrder[targetId]!!.add(card)
                }
            }
        }
    }

    // For two-phase dealing, pause and wait for chooser decision
    if (twoPhase && pendingCards.isNotEmpty()) {
        return state.copy(
            players = state.players.mapValues { (id, p) ->
                p.copy(
                    hand = hands[id]!!.sorted(),
                    isDealer = state.playerOrder.indexOf(id) == state.dealerIndex
                )
            },
            talon = talon.toList(),
            dealing = DealingState(
                phase = DealingPhase.WAITING_FOR_TRUMP,
                pattern = pattern,
                currentStepIndex = pattern.steps.size,
                deckPosition = deckPos,
                deck = deck,
                chooserId = chooserId,
                pendingCards = pendingCards.toList(),
                dealOrder = dealOrder.mapValues { it.value.toList() },
                decisionGate = DecisionGate.trumpSelection(chooserId)
            ),
            currentPlayerIndex = state.playerOrder.indexOf(chooserId)
        )
    }

    // Complete dealing (non-two-phase or no pending cards)
    return state.copy(
        players = state.players.mapValues { (id, p) ->
            p.copy(
                hand = hands[id]!!.sorted(),
                isDealer = state.playerOrder.indexOf(id) == state.dealerIndex
            )
        },
        talon = talon.toList(),
        dealing = DealingState(
            phase = DealingPhase.COMPLETE,
            pattern = pattern,
            currentStepIndex = pattern.steps.size,
            deckPosition = deckPos,
            deck = deck,
            chooserId = chooserId,
            dealOrder = dealOrder.mapValues { it.value.toList() }
        ),
        phase = GamePhase.BIDDING,
        currentPlayerIndex = (state.dealerIndex + 1) % 3
    )
}

/**
 * Chooser selects trump by placing a card face-down.
 * Card is removed from hand, pending cards added, then card returned after reveal.
 */
private fun chooseTrumpReducer(state: GameState, action: GameAction.ChooseTrump): GameState {
    val dealing = state.dealing
    val chooserId = dealing.chooserId!!
    val chooser = state.players[chooserId]!!
    val trumpCard = action.card

    // Remove trump card from hand temporarily (placed on desk)
    val handWithoutTrump = chooser.hand - trumpCard

    // Add pending cards to hand (chooser now has: 7 - 1 + pending cards)
    val newHand = handWithoutTrump + dealing.pendingCards

    // Return trump card to hand (after reveal) - now has full 10 cards
    val finalHand = newHand + trumpCard

    return state.copy(
        players = state.players + (chooserId to chooser.copy(hand = finalHand)),
        trump = trumpCard.suit, // Derive suit from card
        trumpCard = trumpCard,  // Store the specific card (visible to all)
        gameType = GameType.GAME, // Default game type when choosing trump early
        declarerId = chooserId,
        dealing = dealing.copy(
            phase = DealingPhase.COMPLETE,
            pendingCards = emptyList(),
            trumpCard = trumpCard,
            decisionGate = null // Decision made, clear gate
        ),
        phase = GamePhase.TALON_EXCHANGE,
        currentPlayerIndex = state.playerOrder.indexOf(chooserId)
    )
}

/**
 * Chooser passes during dealing pause.
 * Moves pending cards to chooser's hand and proceeds to normal bidding.
 */
private fun chooserPassReducer(state: GameState, action: GameAction.ChooserPass): GameState {
    // Move pending cards to chooser's hand
    val stateAfterDeal = movePendingCardsToHand(state)

    // Continue to bidding without setting trump
    return stateAfterDeal.copy(
        phase = GamePhase.BIDDING,
        currentPlayerIndex = (state.dealerIndex + 1) % 3
    )
}

/**
 * Move pending cards (on table) to chooser's hand.
 */
private fun movePendingCardsToHand(state: GameState): GameState {
    val dealing = state.dealing
    val chooserId = dealing.chooserId!!
    val chooser = state.players[chooserId]!!

    // Add pending cards to chooser's hand
    val newHand = (chooser.hand + dealing.pendingCards)
    return state.copy(
        players = state.players + (chooserId to chooser.copy(hand = newHand)),
        dealing = dealing.copy(
            phase = DealingPhase.COMPLETE,
            pendingCards = emptyList(),
            decisionGate = null // Decision made, clear gate
        )
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
        val gameType = state.bidding.currentBid ?: GameType.GAME
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
    val newHand = (declarer.hand + state.talon).filterNot { it in action.cardsToDiscard }
    val nextPhase = if (state.gameType?.requiresTrump == true && state.trump == null) {
        GamePhase.TRUMP_SELECTION
    } else {
        GamePhase.PLAYING
    }

    val leadPlayerId = state.playerOrder[(state.dealerIndex + 1) % 3]
    return state.copy(
        players = state.players + (action.playerId to declarer.copy(hand = newHand)),
        talon = action.cardsToDiscard,
        phase = nextPhase,
        trick = if (nextPhase == GamePhase.PLAYING) TrickState(leadPlayerId = leadPlayerId) else state.trick,
        currentPlayerIndex = if (nextPhase == GamePhase.PLAYING) {
            state.playerOrder.indexOf(leadPlayerId)
        } else {
            state.currentPlayerIndex
        }
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
