package cz.lbenda.games.engine.model

import kotlinx.serialization.Serializable

/**
 * Represents a position of a card in the game.
 * Part of the Universal Card Model.
 */
@Serializable
data class CardPosition(
    /** ID of the place (e.g., "hand", "talon", "table", "discard") */
    val placeId: String,
    /** List of player IDs who can see the card at this position. Empty if no one, or special value for everyone. */
    val visibility: List<String> = emptyList(),
    /** Order of the card in the place, if applicable. */
    val order: Int = 0
)

/**
 * Configuration for a location where cards can be placed.
 */
@Serializable
data class Place(
    val id: String,
    val name: String,
    val capacity: Int? = null,
    val isOrdered: Boolean = false
)
