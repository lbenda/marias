package cz.lbenda.games.marias.engine.reducer

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.model.Card
import cz.lbenda.games.marias.engine.model.Rank
import cz.lbenda.games.marias.engine.model.Suit
import cz.lbenda.games.marias.engine.state.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class TwoPhaseDealingTest {

    private fun createOrderedDeck(): List<Card> {
        // Create a predictable deck order for testing
        val deck = mutableListOf<Card>()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(Card(suit, rank))
            }
        }
        return deck
    }

    private fun setupPlayers(): GameState {
        var state = GameState(gameId = "test")
        state = reduce(state, GameAction.JoinGame("p1", "Alice"))  // dealer (index 0)
        state = reduce(state, GameAction.JoinGame("p2", "Bob"))    // chooser (index 1)
        state = reduce(state, GameAction.JoinGame("p3", "Charlie"))
        state = reduce(state, GameAction.StartGame("p1"))
        return state
    }

    @Test
    fun `two-phase dealing pauses after chooser receives 7 cards`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))

        // Should be in DEALING phase with sub-phase WAITING_FOR_TRUMP
        assertEquals(GamePhase.DEALING, state.phase)
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)
        assertEquals("p2", state.dealing.chooserId)

        // Chooser (p2) should have 7 cards in hand, 3 pending (on table)
        assertEquals(7, state.players["p2"]!!.hand.size)
        assertEquals(3, state.dealing.pendingCards.size)

        // Other players should have full 10 cards (dealt completely)
        assertEquals(10, state.players["p1"]!!.hand.size)
        assertEquals(10, state.players["p3"]!!.hand.size)

        // Talon should be complete
        assertEquals(2, state.talon.size)

        // Current player should be chooser
        assertEquals(1, state.currentPlayerIndex)
    }

    @Test
    fun `chooser can select trump during pause`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)

        // Chooser selects trump by placing a card from hand
        val trumpCard = state.players["p2"]!!.hand.first() // First card in hand
        state = reduce(state, GameAction.ChooseTrump("p2", trumpCard))

        // Should complete dealing and move to TALON_EXCHANGE
        assertEquals(GamePhase.TALON_EXCHANGE, state.phase)
        assertEquals(DealingPhase.COMPLETE, state.dealing.phase)
        assertEquals(trumpCard.suit, state.trump)
        assertEquals(trumpCard, state.trumpCard)
        assertEquals("p2", state.declarerId)
        assertEquals(GameType.GAME, state.gameType)

        // All players should now have 10 cards (trump card returned to hand)
        assertEquals(10, state.players["p1"]!!.hand.size)
        assertEquals(10, state.players["p2"]!!.hand.size)
        assertEquals(10, state.players["p3"]!!.hand.size)
        assertEquals(2, state.talon.size)

        // Trump card should be in chooser's hand
        assertTrue(trumpCard in state.players["p2"]!!.hand)
    }

    @Test
    fun `chooser can pass during pause and proceed to bidding`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)

        state = reduce(state, GameAction.ChooserPass("p2"))

        // Should complete dealing and move to BIDDING
        assertEquals(GamePhase.BIDDING, state.phase)
        assertEquals(DealingPhase.COMPLETE, state.dealing.phase)
        assertNull(state.trump)
        assertNull(state.declarerId)

        // All players should now have 10 cards
        assertEquals(10, state.players["p1"]!!.hand.size)
        assertEquals(10, state.players["p2"]!!.hand.size)
        assertEquals(10, state.players["p3"]!!.hand.size)
    }

    @Test
    fun `non-chooser cannot select trump during pause`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        // p1 (not chooser) tries to select trump with a card from their hand
        val card = state.players["p1"]!!.hand.first()
        state = reduce(state, GameAction.ChooseTrump("p1", card))

        // Should have error
        assertEquals("Not chooser", state.error)
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)
    }

    @Test
    fun `cannot choose trump when not waiting`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        // Non-two-phase dealing
        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))
        assertEquals(GamePhase.BIDDING, state.phase)

        // Try to choose trump with any card (will fail because not in dealing phase)
        val card = state.players["p2"]!!.hand.first()
        state = reduce(state, GameAction.ChooseTrump("p2", card))
        assertEquals("Not dealing phase", state.error)
    }

    @Test
    fun `deterministic dealing with fixed deck`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        state = reduce(state, GameAction.ChooserPass("p2"))

        // Verify deal order is recorded
        assertNotNull(state.dealing.dealOrder["p1"])
        assertNotNull(state.dealing.dealOrder["p2"])
        assertNotNull(state.dealing.dealOrder["p3"])

        // Chooser should have received cards first (7 cards)
        val chooserDealOrder = state.dealing.dealOrder["p2"]!!
        assertEquals(10, chooserDealOrder.size)

        // First 7 cards for chooser should be first 7 from deck after pattern offset
        // With TWO_PHASE pattern: chooser gets first 7 cards
        assertEquals(Card(Suit.SPADES, Rank.SEVEN), chooserDealOrder[0])
    }

    @Test
    fun `non-two-phase dealing works as before`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))

        // Should go directly to BIDDING
        assertEquals(GamePhase.BIDDING, state.phase)
        assertEquals(DealingPhase.COMPLETE, state.dealing.phase)

        // All players should have 10 cards
        assertEquals(10, state.players["p1"]!!.hand.size)
        assertEquals(10, state.players["p2"]!!.hand.size)
        assertEquals(10, state.players["p3"]!!.hand.size)
        assertEquals(2, state.talon.size)
    }

    @Test
    fun `custom deal pattern validation - invalid total cards`() {
        val invalidPattern = DealPattern(
            steps = listOf(
                DealPattern.DealStep(1, 5),
                DealPattern.DealStep(2, 5),
                DealPattern.DealStep(0, 5)
            ),
            previewCardsForChooser = 5
        )

        val error = invalidPattern.validate()
        assertNotNull(error)
        // Pattern has 15 cards total (5+5+5), no talon - should fail validation
        assertTrue(error.contains("32") || error.contains("10 cards") || error.contains("Talon"))
    }

    @Test
    fun `standard pattern validates correctly`() {
        assertNull(DealPattern.STANDARD.validate())
        assertNull(DealPattern.TWO_PHASE.validate())
        assertNull(DealPattern.oneByOne().validate())
    }

    @Test
    fun `pattern validation - invalid player offset`() {
        val invalidPattern = DealPattern(
            steps = listOf(
                DealPattern.DealStep(1, 10),
                DealPattern.DealStep(2, 10),
                DealPattern.DealStep(3, 10), // Invalid offset
                DealPattern.DealStep(-1, 2)
            ),
            previewCardsForChooser = 7
        )
        val error = invalidPattern.validate()
        assertNotNull(error)
        assertTrue(error.contains("Invalid player offset"))
    }

    @Test
    fun `pattern validation - negative card count`() {
        val invalidPattern = DealPattern(
            steps = listOf(
                DealPattern.DealStep(1, -5), // Invalid negative count
                DealPattern.DealStep(2, 10),
                DealPattern.DealStep(0, 10),
                DealPattern.DealStep(-1, 2)
            ),
            previewCardsForChooser = 7
        )
        val error = invalidPattern.validate()
        assertNotNull(error)
        assertTrue(error.contains("positive"))
    }

    @Test
    fun `pattern validation - invalid preview cards threshold`() {
        val invalidPattern = DealPattern(
            steps = listOf(
                DealPattern.DealStep(1, 10),
                DealPattern.DealStep(2, 10),
                DealPattern.DealStep(0, 10),
                DealPattern.DealStep(-1, 2)
            ),
            previewCardsForChooser = 0 // Invalid - must be 1-10
        )
        val error = invalidPattern.validate()
        assertNotNull(error)
        assertTrue(error.contains("Preview cards"))
    }

    @Test
    fun `pattern validation - empty steps`() {
        val invalidPattern = DealPattern(
            steps = emptyList(),
            previewCardsForChooser = 7
        )
        val error = invalidPattern.validate()
        assertNotNull(error)
        assertTrue(error.contains("at least one step"))
    }

    @Test
    fun `pattern validation rejects invalid pattern during deal action`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        val invalidPattern = DealPattern(
            steps = listOf(
                DealPattern.DealStep(1, 5),
                DealPattern.DealStep(2, 5),
                DealPattern.DealStep(0, 5)
            ),
            previewCardsForChooser = 5
        )

        state = reduce(state, GameAction.DealCards("p1", deck, pattern = invalidPattern))

        // Should have error due to invalid pattern
        val error = state.error
        assertNotNull(error)
        assertTrue(error.contains("32"))
    }

    @Test
    fun `one-by-one dealing pattern works`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()
        val pattern = DealPattern.oneByOne()

        state = reduce(state, GameAction.DealCards("p1", deck, pattern = pattern, twoPhase = true))

        // Should pause after chooser has 7 cards
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)
        assertEquals(7, state.players["p2"]!!.hand.size)

        state = reduce(state, GameAction.ChooserPass("p2"))

        assertEquals(GamePhase.BIDDING, state.phase)
        assertEquals(10, state.players["p1"]!!.hand.size)
        assertEquals(10, state.players["p2"]!!.hand.size)
        assertEquals(10, state.players["p3"]!!.hand.size)
    }

    @Test
    fun `deal order log captures exact sequence`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()
        val pattern = DealPattern.oneByOne()

        state = reduce(state, GameAction.DealCards("p1", deck, pattern = pattern, twoPhase = true))
        state = reduce(state, GameAction.ChooserPass("p2"))

        // Verify that deal order matches the one-by-one pattern
        val p2Order = state.dealing.dealOrder["p2"]!!

        // With one-by-one pattern, chooser (p2) gets cards at positions 0, 3, 6, 9, 12, 15, 18, (talon 21-22), 23, 26, 29
        // First card: deck[0]
        assertEquals(deck[0], p2Order[0])
    }

    @Test
    fun `cannot deal twice`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)

        // Try to deal again
        state = reduce(state, GameAction.DealCards("p1", deck))
        assertEquals("Already dealing", state.error)
    }

    @Test
    fun `deal order preserved after trump selection`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        val dealOrderBeforeTrump = state.dealing.dealOrder.toMap()

        // Select trump with a card from chooser's hand
        val trumpCard = state.players["p2"]!!.hand.first()
        state = reduce(state, GameAction.ChooseTrump("p2", trumpCard))

        // Deal order should still be preserved
        assertEquals(10, state.dealing.dealOrder["p2"]!!.size)
        // Trump card should be recorded
        assertEquals(trumpCard, state.trumpCard)
        assertEquals(trumpCard, state.dealing.trumpCard)
    }

    @Test
    fun `hands are returned in deal order not sorted`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))

        // Hand should be in deal order, not sorted
        val hand = state.players["p2"]!!.hand
        val dealOrder = state.dealing.dealOrder["p2"]!!

        // Hand should match deal order exactly
        assertEquals(dealOrder, hand)
    }

    @Test
    fun `reorder hand changes card order`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))

        val originalHand = state.players["p1"]!!.hand
        val reversedHand = originalHand.reversed()

        state = reduce(state, GameAction.ReorderHand("p1", reversedHand))

        assertNull(state.error)
        assertEquals(reversedHand, state.players["p1"]!!.hand)
    }

    @Test
    fun `reorder hand fails with wrong cards`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))

        // Try to reorder with completely different cards
        val wrongCards = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING)
        )

        state = reduce(state, GameAction.ReorderHand("p1", wrongCards))

        assertEquals("Card count mismatch", state.error)
    }

    @Test
    fun `reorder hand fails with mismatched cards`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = false))

        val hand = state.players["p1"]!!.hand.toMutableList()
        // Replace one card with a card not in hand
        hand[0] = Card(Suit.HEARTS, Rank.SEVEN)

        state = reduce(state, GameAction.ReorderHand("p1", hand))

        assertEquals("Cards don't match current hand", state.error)
    }

    @Test
    fun `choose trump fails with card not in hand`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)

        // Try to choose trump with a card not in chooser's hand
        val cardNotInHand = Card(Suit.HEARTS, Rank.ACE)
        assertFalse(cardNotInHand in state.players["p2"]!!.hand)

        state = reduce(state, GameAction.ChooseTrump("p2", cardNotInHand))

        assertEquals("Card not in hand", state.error)
        assertEquals(DealingPhase.WAITING_FOR_TRUMP, state.dealing.phase)
    }

    @Test
    fun `trump card is visible to all players after selection`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))

        val trumpCard = state.players["p2"]!!.hand.first()
        state = reduce(state, GameAction.ChooseTrump("p2", trumpCard))

        // Trump card should be visible in game state (for all players)
        assertNotNull(state.trumpCard)
        assertEquals(trumpCard, state.trumpCard)
        assertEquals(trumpCard.suit, state.trump)

        // Trump card should also be stored in dealing state
        assertEquals(trumpCard, state.dealing.trumpCard)
    }

    @Test
    fun `decision gate is created during dealing pause`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))

        // Decision gate should be created
        val gate = state.dealing.decisionGate
        assertNotNull(gate)
        assertEquals("p2", gate.playerId)
        assertTrue(state.dealing.canMakeDecision(ChooserDecisionType.SELECT_TRUMP))
        assertTrue(state.dealing.canMakeDecision(ChooserDecisionType.PASS))
        assertFalse(state.dealing.canMakeDecision(ChooserDecisionType.TAKE_TALON))

        // Available decisions should be exposed
        assertEquals(
            setOf(ChooserDecisionType.SELECT_TRUMP, ChooserDecisionType.PASS),
            state.dealing.availableDecisions
        )
    }

    @Test
    fun `decision gate is cleared after trump selection`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertNotNull(state.dealing.decisionGate)

        val trumpCard = state.players["p2"]!!.hand.first()
        state = reduce(state, GameAction.ChooseTrump("p2", trumpCard))

        // Decision gate should be cleared
        assertNull(state.dealing.decisionGate)
        assertTrue(state.dealing.availableDecisions.isEmpty())
    }

    @Test
    fun `decision gate is cleared after pass`() {
        var state = setupPlayers()
        val deck = createOrderedDeck()

        state = reduce(state, GameAction.DealCards("p1", deck, twoPhase = true))
        assertNotNull(state.dealing.decisionGate)

        state = reduce(state, GameAction.ChooserPass("p2"))

        // Decision gate should be cleared
        assertNull(state.dealing.decisionGate)
        assertTrue(state.dealing.availableDecisions.isEmpty())
    }
}
