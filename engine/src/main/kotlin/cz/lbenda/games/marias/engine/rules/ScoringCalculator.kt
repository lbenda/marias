package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GameState
import cz.lbenda.games.marias.engine.state.GameType

class ScoringCalculator {

    data class RoundResult(
        val declarerPoints: Int,
        val declarerWon: Boolean,
        val baseScore: Int,
        val finalScore: Int,
        val bonuses: List<String>
    )

    fun calculateRoundScore(state: GameState): RoundResult {
        val declarer = state.declarer ?: error("No declarer")
        val gameType = state.gameType ?: error("No game type")

        val declarerTrickPoints = declarer.pointsInTricks
        val talonPoints = state.talon.sumOf { it.pointValue }
        val totalDeclarerPoints = declarerTrickPoints + talonPoints

        val baseScore = gameType.baseValue
        val bonuses = mutableListOf<String>()

        val (declarerWon, finalScore) = when (gameType) {
            GameType.HRA -> {
                // Declarer needs more than 50 points to win
                val won = totalDeclarerPoints > 50
                val score = if (won) baseScore else -baseScore
                won to score
            }
            GameType.SEDMA -> {
                // Declarer must win last trick with 7 of trumps
                val lastTrick = state.completedTricks.lastOrNull()
                val wonWithSeven = lastTrick?.cardsPlayed?.lastOrNull()?.let { played ->
                    played.playerId == declarer.playerId &&
                        played.card.rank == Rank.SEDMICKA &&
                        played.card.suit == state.trump
                } ?: false

                val trickResolver = TrickResolver()
                val lastTrickWinner = lastTrick?.let { trickResolver.determineTrickWinner(it, state.trump) }
                val won = wonWithSeven && lastTrickWinner == declarer.playerId

                if (won) bonuses.add("Sedma")
                won to (if (won) baseScore else -baseScore)
            }
            GameType.KILO -> {
                // Declarer needs 100+ points
                val won = totalDeclarerPoints >= 100
                if (won) bonuses.add("Kilo")
                won to (if (won) baseScore else -baseScore)
            }
            GameType.BETL -> {
                // Declarer must not win any trick
                val won = declarer.wonTricks.isEmpty()
                if (won) bonuses.add("Betl")
                won to (if (won) baseScore else -baseScore)
            }
            GameType.DURCH -> {
                // Declarer must win all tricks
                val won = declarer.wonTricks.size == 10
                if (won) bonuses.add("Durch")
                won to (if (won) baseScore else -baseScore)
            }
        }

        return RoundResult(
            declarerPoints = totalDeclarerPoints,
            declarerWon = declarerWon,
            baseScore = baseScore,
            finalScore = finalScore,
            bonuses = bonuses
        )
    }

    fun hasMarriage(hand: List<cz.lbenda.games.marias.engine.model.Card>, suit: Suit): Boolean {
        val hasKing = hand.any { it.suit == suit && it.rank == Rank.KRAL }
        val hasQueen = hand.any { it.suit == suit && it.rank == Rank.SVRSEK }
        return hasKing && hasQueen
    }

    fun getMarriageValue(suit: Suit, trump: Suit?): Int {
        return if (suit == trump) 40 else 20
    }
}
