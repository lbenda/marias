package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

@Serializable
data class BiddingState(
    val currentBid: GameType? = null,
    val bidderId: String? = null,
    val passedPlayers: Set<String> = emptySet()
)
