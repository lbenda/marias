package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType

data class RoundResult(
    val declarerPoints: Int,
    val won: Boolean,
    val score: Int
)

fun calculateScore(state: GameState): RoundResult {
    val declarer = state.players[state.declarerId]!!
    val gameType = state.gameType!!
    val points = declarer.wonCards.sumOf { it.points } + state.talon.sumOf { it.points }

    val won = when (gameType) {
        GameType.HRA -> points > 50
        GameType.SEDMA -> false // TODO: check last trick
        GameType.KILO -> points >= 100
        GameType.BETL -> declarer.wonCards.isEmpty()
        GameType.DURCH -> state.tricksPlayed == 10 && state.players.values.all {
            it.playerId == state.declarerId || it.wonCards.isEmpty()
        }
    }

    return RoundResult(points, won, if (won) gameType.baseValue else -gameType.baseValue)
}

fun hasMarriage(hand: List<Card>, suit: Suit): Boolean =
    hand.any { it.suit == suit && it.rank == Rank.KING } &&
    hand.any { it.suit == suit && it.rank == Rank.QUEEN }
