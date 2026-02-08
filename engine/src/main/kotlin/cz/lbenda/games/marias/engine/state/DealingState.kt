package cz.lbenda.games.marias.engine.state

import cz.lbenda.games.marias.engine.model.Card
import kotlinx.serialization.Serializable

@Serializable
enum class DealingPhase {
    NOT_STARTED,
    PHASE_A,           // Initial dealing until chooser has preview cards
    WAITING_FOR_TRUMP, // Paused, waiting for chooser to select trump
    PHASE_B,           // Remaining cards being dealt
    COMPLETE           // All cards dealt
}

/**
 * Represents a dealing pattern - how cards are distributed in chunks.
 * Each entry is (playerOffset, cardCount) where playerOffset is relative to dealer.
 * Default Mariash pattern: chooser gets 7, others get 5, talon gets 2, then remaining 3+5+5.
 */
@Serializable
data class DealPattern(
    val steps: List<DealStep>,
    val previewCardsForChooser: Int = 7
) {
    @Serializable
    data class DealStep(
        val playerOffset: Int, // 0=dealer, 1=chooser (after dealer), 2=third player, -1=talon
        val cardCount: Int
    )

    companion object {
        /** Standard Mariáš: 7-7-7, 2 talon, 3-3-3 */
        val STANDARD = DealPattern(
            steps = listOf(
                DealStep(1, 7), DealStep(2, 7), DealStep(0, 7), // First round: 7 each
                DealStep(-1, 2),                                 // Talon
                DealStep(1, 3), DealStep(2, 3), DealStep(0, 3)  // Second round: 3 each
            ),
            previewCardsForChooser = 7
        )

        /** Two-phase: chooser gets 7 first, pause, then continues */
        val TWO_PHASE = DealPattern(
            steps = listOf(
                DealStep(1, 7),                                  // Chooser first 7
                DealStep(2, 5), DealStep(0, 5),                  // Others first 5
                DealStep(-1, 2),                                 // Talon
                DealStep(2, 5), DealStep(0, 5),                  // Others remaining 5
                DealStep(1, 3)                                   // Chooser remaining 3
            ),
            previewCardsForChooser = 7
        )

        /** One-by-one dealing (for testing) */
        fun oneByOne(): DealPattern {
            val steps = mutableListOf<DealStep>()
            // 7 rounds of 1 card each to 3 players
            repeat(7) {
                steps.add(DealStep(1, 1))
                steps.add(DealStep(2, 1))
                steps.add(DealStep(0, 1))
            }
            // Talon
            steps.add(DealStep(-1, 2))
            // 3 more rounds
            repeat(3) {
                steps.add(DealStep(1, 1))
                steps.add(DealStep(2, 1))
                steps.add(DealStep(0, 1))
            }
            return DealPattern(steps, previewCardsForChooser = 7)
        }
    }

    /**
     * Validates the deal pattern.
     * Returns error message if invalid, null if valid.
     */
    fun validate(): String? {
        // Validate previewCardsForChooser
        if (previewCardsForChooser < 1 || previewCardsForChooser > 10) {
            return "Preview cards must be between 1 and 10"
        }

        // Validate steps
        if (steps.isEmpty()) {
            return "Pattern must have at least one step"
        }

        val playerCards = mutableMapOf(0 to 0, 1 to 0, 2 to 0)
        var talonCards = 0

        for (step in steps) {
            // Validate player offset
            if (step.playerOffset !in listOf(-1, 0, 1, 2)) {
                return "Invalid player offset: ${step.playerOffset}. Must be -1 (talon), 0, 1, or 2"
            }

            // Validate card count
            if (step.cardCount < 1) {
                return "Card count must be positive"
            }

            if (step.playerOffset == -1) {
                talonCards += step.cardCount
            } else {
                playerCards[step.playerOffset] = (playerCards[step.playerOffset] ?: 0) + step.cardCount
            }
        }

        // Validate total cards
        val totalCards = playerCards.values.sum() + talonCards
        if (totalCards != 32) {
            return "Total cards must be 32, got $totalCards"
        }

        // Validate each player gets 10 cards
        for ((offset, count) in playerCards) {
            if (count != 10) {
                return "Player $offset must receive 10 cards, got $count"
            }
        }

        // Validate talon gets 2 cards
        if (talonCards != 2) {
            return "Talon must have exactly 2 cards, got $talonCards"
        }

        // Validate chooser (offset 1) gets at least previewCardsForChooser total
        val chooserTotal = playerCards[1] ?: 0
        if (chooserTotal < previewCardsForChooser) {
            return "Chooser must receive at least $previewCardsForChooser cards, got $chooserTotal"
        }

        return null
    }

    /** Check if pattern is valid */
    val isValid: Boolean get() = validate() == null
}

/**
 * Tracks the current state of dealing.
 */
@Serializable
data class DealingState(
    val phase: DealingPhase = DealingPhase.NOT_STARTED,
    val pattern: DealPattern = DealPattern.TWO_PHASE,
    val currentStepIndex: Int = 0,
    val deckPosition: Int = 0,
    val deck: List<Card> = emptyList(), // Full deck for deterministic continuation
    val chooserId: String? = null,
    val pendingCards: List<Card> = emptyList(),  // Cards on table for chooser (remaining cards to deal)
    val dealOrder: Map<String, List<Card>> = emptyMap(), // Per-player deal order log
    val trumpCard: Card? = null, // Trump card placed face-down (before reveal)
    val decisionGate: DecisionGate? = null // Generic decision gate for chooser actions
) {
    val isWaitingForChooser: Boolean get() = phase == DealingPhase.WAITING_FOR_TRUMP
    val isComplete: Boolean get() = phase == DealingPhase.COMPLETE

    /** Available decisions at current gate, empty if no gate active */
    val availableDecisions: Set<ChooserDecisionType>
        get() = decisionGate?.availableDecisions ?: emptySet()

    /** Check if a specific decision is available */
    fun canMakeDecision(type: ChooserDecisionType): Boolean =
        decisionGate?.isAvailable(type) ?: false
}
