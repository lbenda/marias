package cz.lbenda.games.marias.engine.rules

import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.GameType
import org.junit.jupiter.api.Test
import kotlin.test.*

class SevenAnnouncementValidationTest {

    @Test
    fun `canAnnounceSevenVariant returns true for non-Seven game types`() {
        val talon = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.EIGHT))

        // Even with trump 7 in talon, non-Seven game types are allowed
        assertTrue(canAnnounceSevenVariant(GameType.GAME, Suit.HEARTS, talon))
        assertTrue(canAnnounceSevenVariant(GameType.HUNDRED, Suit.HEARTS, talon))
        assertTrue(canAnnounceSevenVariant(GameType.MISERE, Suit.HEARTS, talon))
        assertTrue(canAnnounceSevenVariant(GameType.SLAM, Suit.HEARTS, talon))
    }

    @Test
    fun `canAnnounceSevenVariant returns true when trump 7 not in talon`() {
        val talon = listOf(Card(Suit.HEARTS, Rank.EIGHT), Card(Suit.SPADES, Rank.NINE))

        // Trump 7 not in talon, Seven variants allowed
        assertTrue(canAnnounceSevenVariant(GameType.SEVEN, Suit.HEARTS, talon))
        assertTrue(canAnnounceSevenVariant(GameType.HUNDRED_SEVEN, Suit.HEARTS, talon))
        assertTrue(canAnnounceSevenVariant(GameType.TWO_SEVENS, Suit.HEARTS, talon))
    }

    @Test
    fun `canAnnounceSevenVariant returns false when trump 7 in talon for Seven`() {
        val talon = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.EIGHT))

        // Trump is HEARTS, and hearts 7 is in talon
        assertFalse(canAnnounceSevenVariant(GameType.SEVEN, Suit.HEARTS, talon))
    }

    @Test
    fun `canAnnounceSevenVariant returns false when trump 7 in talon for Hundred-Seven`() {
        val talon = listOf(Card(Suit.SPADES, Rank.SEVEN), Card(Suit.HEARTS, Rank.EIGHT))

        // Trump is SPADES, and spades 7 is in talon
        assertFalse(canAnnounceSevenVariant(GameType.HUNDRED_SEVEN, Suit.SPADES, talon))
    }

    @Test
    fun `canAnnounceSevenVariant returns false when trump 7 in talon for Two-Sevens`() {
        val talon = listOf(Card(Suit.CLUBS, Rank.SEVEN), Card(Suit.HEARTS, Rank.KING))

        // Trump is CLUBS, and clubs 7 is in talon
        assertFalse(canAnnounceSevenVariant(GameType.TWO_SEVENS, Suit.CLUBS, talon))
    }

    @Test
    fun `canAnnounceSevenVariant allows Seven when different suit 7 in talon`() {
        val talon = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.EIGHT))

        // Trump is SPADES, hearts 7 is in talon but that's not trump 7
        assertTrue(canAnnounceSevenVariant(GameType.SEVEN, Suit.SPADES, talon))
    }

    @Test
    fun `canAnnounceSevenVariant handles null gameType`() {
        val talon = listOf(Card(Suit.HEARTS, Rank.SEVEN), Card(Suit.SPADES, Rank.EIGHT))

        // Null game type should return true (no restriction)
        assertTrue(canAnnounceSevenVariant(null, Suit.HEARTS, talon))
    }

    @Test
    fun `canAnnounceSevenVariant handles empty talon`() {
        val talon = emptyList<Card>()

        // Empty talon means trump 7 was not discarded
        assertTrue(canAnnounceSevenVariant(GameType.SEVEN, Suit.HEARTS, talon))
    }
}
