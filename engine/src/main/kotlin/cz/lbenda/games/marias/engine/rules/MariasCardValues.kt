package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.DeckType
import cz.lbenda.games.marias.engine.model.Rank

/**
 * Mariáš-specific card point values and strength ordering.
 * In Mariáš, the card strength order is: 7 < 8 < 9 < 10 < Jack < Queen < King < Ace
 * Point values: 7,8,9 = 0; Jack = 2; Queen = 3; King = 4; 10 = 10; Ace = 11
 */
object MariasCardValues {

    val DECK_TYPE = DeckType.PIQUET_32

    private val POINT_VALUES = mapOf(
        Rank.SEVEN to 0,
        Rank.EIGHT to 0,
        Rank.NINE to 0,
        Rank.TEN to 10,
        Rank.JACK to 2,
        Rank.QUEEN to 3,
        Rank.KING to 4,
        Rank.ACE to 11
    )

    // Strength order for trick-taking (higher = stronger)
    private val STRENGTH_ORDER = mapOf(
        Rank.SEVEN to 1,
        Rank.EIGHT to 2,
        Rank.NINE to 3,
        Rank.TEN to 4,
        Rank.JACK to 5,
        Rank.QUEEN to 6,
        Rank.KING to 7,
        Rank.ACE to 8
    )

    fun getPointValue(card: Card): Int = POINT_VALUES[card.rank] ?: 0

    fun getPointValue(rank: Rank): Int = POINT_VALUES[rank] ?: 0

    fun getStrength(card: Card): Int = STRENGTH_ORDER[card.rank] ?: 0

    fun getStrength(rank: Rank): Int = STRENGTH_ORDER[rank] ?: 0

    fun getTotalDeckPoints(): Int = DECK_TYPE.createDeck().sumOf { getPointValue(it) }

    fun createDeck(): List<Card> = DECK_TYPE.createDeck()

    fun createShuffledDeck(): List<Card> = DECK_TYPE.createShuffledDeck()

    /**
     * Compare cards by Mariáš strength within the same suit.
     * Returns positive if card1 is stronger, negative if card2 is stronger.
     */
    fun compareByStrength(card1: Card, card2: Card): Int {
        return getStrength(card1).compareTo(getStrength(card2))
    }
}
