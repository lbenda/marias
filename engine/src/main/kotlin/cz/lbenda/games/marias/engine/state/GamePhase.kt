package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

@Serializable
enum class GamePhase {
    WAITING_FOR_PLAYERS,
    DEALING,
    BIDDING,
    TALON_EXCHANGE,
    TRUMP_SELECTION,
    PLAYING,
    SCORING,
    FINISHED
}
