package cz.lbenda.games.marias.engine.action

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.DealPattern
import cz.lbenda.games.marias.engine.state.GameType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class GameAction {
    abstract val playerId: String

    @Serializable @SerialName("join")
    data class JoinGame(override val playerId: String, val playerName: String) : GameAction()

    @Serializable @SerialName("leave")
    data class LeaveGame(override val playerId: String) : GameAction()

    @Serializable @SerialName("start")
    data class StartGame(override val playerId: String) : GameAction()

    @Serializable @SerialName("deal")
    data class DealCards(
        override val playerId: String,
        val deck: List<Card>? = null,
        val pattern: DealPattern? = null,
        val twoPhase: Boolean = true
    ) : GameAction()

    /** Chooser selects trump by placing a card face-down. Card determines trump suit. */
    @Serializable @SerialName("choosetrump")
    data class ChooseTrump(override val playerId: String, val card: Card) : GameAction()

    /** Chooser passes during dealing pause. Resumes dealing and proceeds to normal bidding. */
    @Serializable @SerialName("chooserpass")
    data class ChooserPass(override val playerId: String) : GameAction()

    @Serializable @SerialName("bid")
    data class PlaceBid(override val playerId: String, val gameType: GameType) : GameAction()

    @Serializable @SerialName("pass")
    data class Pass(override val playerId: String) : GameAction()

    @Serializable @SerialName("exchange")
    data class ExchangeTalon(override val playerId: String, val cardsToDiscard: List<Card>) : GameAction()

    @Serializable @SerialName("trump")
    data class SelectTrump(override val playerId: String, val trump: Suit) : GameAction()

    @Serializable @SerialName("play")
    data class PlayCard(override val playerId: String, val card: Card) : GameAction()

    @Serializable @SerialName("marriage")
    data class DeclareMarriage(override val playerId: String, val suit: Suit) : GameAction()

    @Serializable @SerialName("newround")
    data class StartNewRound(override val playerId: String) : GameAction()

    /** Reorder cards in hand. Cards must match current hand exactly. */
    @Serializable @SerialName("reorderhand")
    data class ReorderHand(override val playerId: String, val cards: List<Card>) : GameAction()
}
