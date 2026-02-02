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
    val declarerPlayerId: String? = null,
    val biddingState: BiddingState = BiddingState(),
    val currentTrick: TrickState = TrickState(),
    val completedTricks: List<TrickState> = emptyList(),
    val roundNumber: Int = 1,
    val errorMessage: String? = null
) {
    val currentPlayerId: String? get() = playerOrder.getOrNull(currentPlayerIndex)

    val currentPlayer: PlayerState? get() = currentPlayerId?.let { players[it] }

    val dealerPlayerId: String? get() = playerOrder.getOrNull(dealerIndex)

    val isGameFull: Boolean get() = players.size == 3

    val declarer: PlayerState? get() = declarerPlayerId?.let { players[it] }

    fun getPlayer(playerId: String): PlayerState? = players[playerId]

    fun getPlayerByPosition(position: Int): PlayerState? {
        val playerId = playerOrder.getOrNull(position)
        return playerId?.let { players[it] }
    }

    fun nextPlayerIndex(): Int = (currentPlayerIndex + 1) % 3

    fun withIncrementedVersion(): GameState = copy(version = version + 1)
}
