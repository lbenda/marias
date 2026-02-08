package cz.lbenda.games.engine.state

import kotlinx.serialization.Serializable

/**
 * Base interface for all game states.
 * Allows the engine to handle different game types polymorphically.
 */
@Serializable
abstract class BaseGameState {
    abstract val gameId: String
    abstract val version: Long
    abstract val players: Map<String, BasePlayerState>
    abstract val playerOrder: List<String>
    abstract val currentPlayerIndex: Int
    abstract val error: String?
}

/**
 * Base interface for player states.
 */
@Serializable
abstract class BasePlayerState {
    abstract val playerId: String
    abstract val name: String
}
