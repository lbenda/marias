package cz.lbenda.games.marias.server.dto

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.rules.validCards
import cz.lbenda.games.marias.engine.state.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(val playerId: String, val playerName: String)

@Serializable
data class ActionRequest(val action: GameAction)

@Serializable
data class GameResponse(
    val gameId: String,
    val version: Long,
    val phase: GamePhase,
    val players: List<PlayerDto>,
    val currentPlayerId: String?,
    val dealerId: String?,
    val trump: Suit?,
    val gameType: GameType?,
    val declarerId: String?,
    val trick: TrickDto,
    val tricksPlayed: Int,
    val roundNumber: Int,
    val error: String?
)

@Serializable
data class PlayerDto(
    val playerId: String,
    val name: String,
    val cardCount: Int,
    val points: Int,
    val isDealer: Boolean
)

@Serializable
data class TrickDto(
    val cards: List<PlayedCardDto>,
    val leadPlayerId: String?
)

@Serializable
data class PlayedCardDto(val playerId: String, val card: Card)

@Serializable
data class HandResponse(val hand: List<Card>, val validCards: List<Card>)

@Serializable
data class GameListItem(val gameId: String, val phase: GamePhase, val playerCount: Int)

@Serializable
data class BiddingResponse(
    val currentBid: GameType?,
    val bidderId: String?,
    val passedPlayers: List<String>
)

fun GameState.toResponse() = GameResponse(
    gameId = gameId,
    version = version,
    phase = phase,
    players = playerOrder.mapNotNull { players[it]?.toDto() },
    currentPlayerId = playerOrder.getOrNull(currentPlayerIndex),
    dealerId = playerOrder.getOrNull(dealerIndex),
    trump = trump,
    gameType = gameType,
    declarerId = declarerId,
    trick = trick.toDto(),
    tricksPlayed = tricksPlayed,
    roundNumber = roundNumber,
    error = error
)

fun PlayerState.toDto() = PlayerDto(
    playerId = playerId,
    name = name,
    cardCount = hand.size,
    points = wonCards.sumOf { it.points },
    isDealer = isDealer
)

fun TrickState.toDto() = TrickDto(
    cards = cards.map { PlayedCardDto(it.first, it.second) },
    leadPlayerId = leadPlayerId
)

fun GameState.toListItem() = GameListItem(gameId, phase, players.size)

fun GameState.handResponse(playerId: String) = HandResponse(
    hand = players[playerId]?.hand ?: emptyList(),
    validCards = validCards(this, playerId)
)

fun GameState.biddingResponse() = BiddingResponse(
    currentBid = bidding.currentBid,
    bidderId = bidding.bidderId,
    passedPlayers = bidding.passedPlayers.toList()
)
