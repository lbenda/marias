package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.createShuffledDeck

// Deal: 7 cards to each player, 2 to talon, 3 more to each player
fun dealCards(deck: List<Card>, players: List<String>): Pair<Map<String, List<Card>>, List<Card>> {
    var i = 0
    val hands = players.associateWith { mutableListOf<Card>() }

    // First round: 7 cards each
    players.forEach { p -> repeat(7) { hands[p]!!.add(deck[i++]) } }

    // Talon: 2 cards
    val talon = listOf(deck[i++], deck[i++])

    // Second round: 3 cards each
    players.forEach { p -> repeat(3) { hands[p]!!.add(deck[i++]) } }

    return hands.mapValues { it.value.toList() } to talon
}
