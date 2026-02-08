package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.engine.state.BasePlayerState
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    override val playerId: String,
    override val name: String,
    val hand: List<Card> = emptyList(),
    val wonCards: List<Card> = emptyList(),
    val score: Int = 0,
    val hasPassed: Boolean = false,
    val isDealer: Boolean = false
) : BasePlayerState()
