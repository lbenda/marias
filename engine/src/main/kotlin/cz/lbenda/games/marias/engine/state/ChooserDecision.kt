package cz.lbenda.games.marias.engine.state

import kotlinx.serialization.Serializable

/**
 * Types of decisions a chooser can make during the dealing pause.
 */
@Serializable
enum class ChooserDecisionType {
    /** Select trump by placing a card face-down */
    SELECT_TRUMP,
    /** Pass - decline to choose trump, proceed to bidding */
    PASS,
    /** Take talon and declare Misere or Slam (future) */
    TAKE_TALON
}

/**
 * Represents a decision gate where the game pauses waiting for chooser input.
 * This is a generic mechanism that can be extended for different contract types.
 */
@Serializable
data class DecisionGate(
    /** Player who must make the decision */
    val playerId: String,
    /** Available decision types at this gate */
    val availableDecisions: Set<ChooserDecisionType>,
    /** Whether a decision is mandatory (must choose one) or can timeout */
    val mandatory: Boolean = true
) {
    /** Check if a specific decision type is available */
    fun isAvailable(type: ChooserDecisionType): Boolean = type in availableDecisions

    companion object {
        /** Standard gate after initial deal - chooser can select trump or pass */
        fun trumpSelection(chooserId: String) = DecisionGate(
            playerId = chooserId,
            availableDecisions = setOf(ChooserDecisionType.SELECT_TRUMP, ChooserDecisionType.PASS),
            mandatory = true
        )

        /** Gate for talon response - other players can say "Good" or take talon (future) */
        fun talonResponse(playerId: String) = DecisionGate(
            playerId = playerId,
            availableDecisions = setOf(ChooserDecisionType.PASS, ChooserDecisionType.TAKE_TALON),
            mandatory = true
        )
    }
}
