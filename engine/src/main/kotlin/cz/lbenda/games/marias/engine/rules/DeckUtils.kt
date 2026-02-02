package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card

object DeckUtils {

    /**
     * Deals cards according to Mariáš pattern: 7-5-5-2-3-5-5
     * First 7 to each player, then 2 to talon, then 5 more to each player
     * Returns map of player hands and talon
     */
    fun dealCards(
        deck: List<Card>,
        playerOrder: List<String>
    ): Pair<Map<String, List<Card>>, List<Card>> {
        require(deck.size == 32) { "Deck must have exactly 32 cards" }
        require(playerOrder.size == 3) { "Must have exactly 3 players" }

        val hands = mutableMapOf<String, MutableList<Card>>()
        playerOrder.forEach { hands[it] = mutableListOf() }

        var cardIndex = 0

        // First round: 7 cards to each player
        for (player in playerOrder) {
            repeat(7) {
                hands[player]!!.add(deck[cardIndex++])
            }
        }

        // Talon: 2 cards
        val talon = listOf(deck[cardIndex++], deck[cardIndex++])

        // Second round: 3 cards to each player
        for (player in playerOrder) {
            repeat(3) {
                hands[player]!!.add(deck[cardIndex++])
            }
        }

        return hands.mapValues { it.value.toList() } to talon
    }

    fun createShuffledDeck(): List<Card> = Card.createShuffledDeck()

    fun createDeck(): List<Card> = Card.createDeck()
}
