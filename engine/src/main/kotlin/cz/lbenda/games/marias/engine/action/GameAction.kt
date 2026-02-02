package cz.lbenda.games.marias.engine.action

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GameType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class GameAction {
    abstract val playerId: String

    @Serializable
    @SerialName("join_game")
    data class JoinGame(
        override val playerId: String,
        val playerName: String
    ) : GameAction()

    @Serializable
    @SerialName("leave_game")
    data class LeaveGame(
        override val playerId: String
    ) : GameAction()

    @Serializable
    @SerialName("start_game")
    data class StartGame(
        override val playerId: String
    ) : GameAction()

    @Serializable
    @SerialName("deal_cards")
    data class DealCards(
        override val playerId: String,
        val shuffledDeck: List<Card>? = null
    ) : GameAction()

    @Serializable
    @SerialName("place_bid")
    data class PlaceBid(
        override val playerId: String,
        val gameType: GameType
    ) : GameAction()

    @Serializable
    @SerialName("pass")
    data class Pass(
        override val playerId: String
    ) : GameAction()

    @Serializable
    @SerialName("exchange_talon")
    data class ExchangeTalon(
        override val playerId: String,
        val cardsToDiscard: List<Card>
    ) : GameAction()

    @Serializable
    @SerialName("select_trump")
    data class SelectTrump(
        override val playerId: String,
        val trump: Suit
    ) : GameAction()

    @Serializable
    @SerialName("play_card")
    data class PlayCard(
        override val playerId: String,
        val card: Card
    ) : GameAction()

    @Serializable
    @SerialName("declare_marriage")
    data class DeclareMarriage(
        override val playerId: String,
        val suit: Suit
    ) : GameAction()

    @Serializable
    @SerialName("start_new_round")
    data class StartNewRound(
        override val playerId: String
    ) : GameAction()
}
