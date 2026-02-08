package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.engine.state.BaseGameState
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    override val gameId: String,
    override val version: Long = 0,
    val phase: GamePhase = GamePhase.WAITING_FOR_PLAYERS,
    override val players: Map<String, PlayerState> = emptyMap(),
    override val playerOrder: List<String> = emptyList(),
    val dealerIndex: Int = 0,
    override val currentPlayerIndex: Int = 0,
    val talon: List<Card> = emptyList(),
    val trump: Suit? = null,
    val trumpCard: Card? = null, // The specific card used to declare trump (visible to all after reveal)
    val gameType: GameType? = null,
    val declarerId: String? = null,
    val dealing: DealingState = DealingState(),
    val bidding: BiddingState = BiddingState(),
    val trick: TrickState = TrickState(),
    val tricksPlayed: Int = 0,
    val roundNumber: Int = 1,
    override val error: String? = null
) : BaseGameState() {
    /** The player who chooses trump first (player after dealer) */
    val chooserId: String? get() = playerOrder.getOrNull((dealerIndex + 1) % playerOrder.size)
}
