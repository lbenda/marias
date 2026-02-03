package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val gameId: String,
    val version: Long = 0,
    val phase: GamePhase = GamePhase.WAITING_FOR_PLAYERS,
    val players: Map<String, PlayerState> = emptyMap(),
    val playerOrder: List<String> = emptyList(),
    val dealerIndex: Int = 0,
    val currentPlayerIndex: Int = 0,
    val talon: List<Card> = emptyList(),
    val trump: Suit? = null,
    val gameType: GameType? = null,
    val declarerId: String? = null,
    val bidding: BiddingState = BiddingState(),
    val trick: TrickState = TrickState(),
    val tricksPlayed: Int = 0,
    val roundNumber: Int = 1,
    val error: String? = null
)
