package cz.lbenda.games.marias.server.dto

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.rules.MariasCardValues
import cz.lbenda.games.marias.engine.state.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(
    val creatorPlayerId: String,
    val creatorPlayerName: String
)

@Serializable
data class CreateGameResponse(
    val gameId: String,
    val state: GameStateDto
)

@Serializable
data class GameStateDto(
    val gameId: String,
    val version: Long,
    val phase: GamePhase,
    val players: List<PlayerInfoDto>,
    val playerOrder: List<String>,
    val dealerPlayerId: String?,
    val currentPlayerId: String?,
    val trump: Suit?,
    val gameType: GameType?,
    val declarerPlayerId: String?,
    val currentTrick: TrickStateDto,
    val completedTricksCount: Int,
    val roundNumber: Int,
    val errorMessage: String?
)

@Serializable
data class PlayerInfoDto(
    val playerId: String,
    val name: String,
    val cardCount: Int,
    val tricksWonCount: Int,
    val pointsInTricks: Int,
    val hasPassed: Boolean,
    val isDealer: Boolean,
    val seatPosition: Int
)

@Serializable
data class TrickStateDto(
    val cardsPlayed: List<PlayedCardDto>,
    val leadPlayerId: String?,
    val trickNumber: Int
)

@Serializable
data class PlayedCardDto(
    val playerId: String,
    val card: Card
)

@Serializable
data class PlayerHandDto(
    val playerId: String,
    val hand: List<Card>,
    val validCards: List<Card>
)

@Serializable
data class ActionRequest(
    val action: GameAction
)

@Serializable
data class ActionResponse(
    val success: Boolean,
    val state: GameStateDto,
    val errorMessage: String?
)

@Serializable
data class GameListResponse(
    val games: List<GameSummaryDto>
)

@Serializable
data class GameSummaryDto(
    val gameId: String,
    val phase: GamePhase,
    val playerCount: Int,
    val roundNumber: Int
)

@Serializable
data class TalonDto(
    val cards: List<Card>
)

@Serializable
data class BiddingStateDto(
    val currentBid: GameType?,
    val currentBidder: String?,
    val passedPlayers: List<String>,
    val availableBids: List<GameType>
)

object DtoMapper {

    fun toGameStateDto(state: GameState): GameStateDto {
        return GameStateDto(
            gameId = state.gameId,
            version = state.version,
            phase = state.phase,
            players = state.playerOrder.mapNotNull { playerId ->
                state.players[playerId]?.let { toPlayerInfoDto(it) }
            },
            playerOrder = state.playerOrder,
            dealerPlayerId = state.dealerPlayerId,
            currentPlayerId = state.currentPlayerId,
            trump = state.trump,
            gameType = state.gameType,
            declarerPlayerId = state.declarerPlayerId,
            currentTrick = toTrickStateDto(state.currentTrick),
            completedTricksCount = state.completedTricks.size,
            roundNumber = state.roundNumber,
            errorMessage = state.errorMessage
        )
    }

    fun toPlayerInfoDto(player: PlayerState): PlayerInfoDto {
        return PlayerInfoDto(
            playerId = player.playerId,
            name = player.name,
            cardCount = player.hand.size,
            tricksWonCount = player.tricksWonCount,
            pointsInTricks = player.allWonCards.sumOf { MariasCardValues.getPointValue(it) },
            hasPassed = player.hasPassed,
            isDealer = player.isDealer,
            seatPosition = player.seatPosition
        )
    }

    fun toTrickStateDto(trick: TrickState): TrickStateDto {
        return TrickStateDto(
            cardsPlayed = trick.cardsPlayed.map { PlayedCardDto(it.playerId, it.card) },
            leadPlayerId = trick.leadPlayerId,
            trickNumber = trick.trickNumber
        )
    }

    fun toPlayerHandDto(state: GameState, playerId: String, validCards: List<Card>): PlayerHandDto {
        val player = state.players[playerId] ?: error("Player not found")
        return PlayerHandDto(
            playerId = playerId,
            hand = player.hand,
            validCards = validCards
        )
    }

    fun toBiddingStateDto(state: GameState): BiddingStateDto {
        val bidding = state.biddingState
        val availableBids = GameType.entries.filter { bidding.canBid(it) }
        return BiddingStateDto(
            currentBid = bidding.currentBid,
            currentBidder = bidding.currentBidder,
            passedPlayers = bidding.passedPlayers.toList(),
            availableBids = availableBids
        )
    }

    fun toGameSummaryDto(state: GameState): GameSummaryDto {
        return GameSummaryDto(
            gameId = state.gameId,
            phase = state.phase,
            playerCount = state.players.size,
            roundNumber = state.roundNumber
        )
    }
}
