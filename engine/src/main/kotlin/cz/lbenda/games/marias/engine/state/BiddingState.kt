package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

@Serializable
data class BiddingState(
    val currentBid: GameType? = null,
    val currentBidder: String? = null,
    val passedPlayers: Set<String> = emptySet(),
    val biddingOrder: List<String> = emptyList(),
    val currentBidderIndex: Int = 0
) {
    val activeBiddersCount: Int get() = biddingOrder.size - passedPlayers.size

    val nextBidderIndex: Int get() {
        var next = (currentBidderIndex + 1) % biddingOrder.size
        while (passedPlayers.contains(biddingOrder[next]) && activeBiddersCount > 0) {
            next = (next + 1) % biddingOrder.size
        }
        return next
    }

    val nextBidderId: String? get() = if (activeBiddersCount > 0) biddingOrder[nextBidderIndex] else null

    fun canBid(gameType: GameType): Boolean {
        return currentBid == null || gameType.ordinal > currentBid.ordinal
    }
}
