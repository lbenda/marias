package cz.lbenda.games.engine.rules

/**
 * Registry for managing multiple versions and types of games.
 * Mapping `ruleSetId` to `GameRuleSet` implementation.
 * Follows naming convention: `game:variant:vX` (e.g., `marias:three-player:v1`).
 */
object RuleSetRegistry {
    private val ruleSets = mutableMapOf<String, GameRuleSet>()

    fun register(id: String, ruleSet: GameRuleSet) {
        ruleSets[id] = ruleSet
    }

    fun get(id: String): GameRuleSet? {
        return ruleSets[id]
    }

    fun listIds(): Set<String> {
        return ruleSets.keys
    }

    /**
     * Parse and validate rule set ID.
     * Convention: game:variant:vX
     */
    fun isValidId(id: String): Boolean {
        val parts = id.split(":")
        return parts.size == 3 && parts[2].startsWith("v") && parts[2].substring(1).toIntOrNull() != null
    }
}
