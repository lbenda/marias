package cz.lbenda.games.engine.rules

import cz.lbenda.games.marias.engine.action.GameAction
import cz.lbenda.games.marias.engine.state.GameState

/**
 * Interface for game rules.
 * Centralizes all game logic, allowing the server to tell clients exactly what actions are possible at any moment.
 */
interface GameRuleSet {
    /**
     * Returns a list of possible actions for the given player in the current state.
     */
    fun possibleActions(state: GameState, playerId: String): List<GameAction>

    /**
     * Reduces the state by applying the given action.
     * Returns the new state.
     * Should throw an exception or return an error state if the action is invalid.
     */
    fun reduce(state: GameState, action: GameAction): GameState

    /**
     * Validates if the given action is valid for the current state.
     */
    fun validate(state: GameState, action: GameAction): Boolean {
        return possibleActions(state, action.playerId).contains(action)
    }
}
