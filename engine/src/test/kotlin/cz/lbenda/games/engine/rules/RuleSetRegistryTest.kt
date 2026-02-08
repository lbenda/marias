package cz.lbenda.games.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GameState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RuleSetRegistryTest {

    private val dummyRuleSet = object : GameRuleSet {
        override fun possibleActions(state: GameState, playerId: String): List<GameAction> = emptyList()
        override fun reduce(state: GameState, action: GameAction): GameState = state
    }

    @Test
    fun testRegisterAndGet() {
        val id = "marias:three-player:v1"
        RuleSetRegistry.register(id, dummyRuleSet)
        assertEquals(dummyRuleSet, RuleSetRegistry.get(id))
    }

    @Test
    fun testIsValidId() {
        assertTrue(RuleSetRegistry.isValidId("marias:three-player:v1"))
        assertTrue(RuleSetRegistry.isValidId("marias:three-player:v10"))
        assertFalse(RuleSetRegistry.isValidId("marias:three-player"))
        assertFalse(RuleSetRegistry.isValidId("marias:three-player:1"))
        assertFalse(RuleSetRegistry.isValidId("marias:v1"))
        assertFalse(RuleSetRegistry.isValidId("marias:three-player:v"))
    }

    @Test
    fun testListIds() {
        val id1 = "game1:variant:v1"
        val id2 = "game2:variant:v1"
        RuleSetRegistry.register(id1, dummyRuleSet)
        RuleSetRegistry.register(id2, dummyRuleSet)
        val ids = RuleSetRegistry.listIds()
        assertTrue(ids.contains(id1))
        assertTrue(ids.contains(id2))
    }
}
