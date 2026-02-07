# A-013: Rule-Based Engine and Action Provider

## Status
Proposed

## Context
The current game logic is split between validation in the engine (`GameRules.validate`) and action preparation in the UI. This leads to:
1.  **Duplicated Logic:** The UI needs to "guess" which actions are valid to show the correct buttons or highlight playable cards.
2.  **Increased UI Complexity:** The React/JS layer must understand game rules to provide a good user experience.
3.  **Maintenance Burden:** Adding a new game rule or action requires changes in both the Kotlin engine and all UI clients.

We need a way to centralize all game possibilities in the engine so the UI can be purely "data-driven."

## Decision
We will implement a **Custom Rule-Based Engine** using an **Action Provider** pattern, designed for **Multi-Game Support**.

Key technical details:
1.  **RuleSet Interface:** Each game type (e.g., Mariáš, Prší) will be defined as a `RuleSet` implementation. This interface will be the "Single Source of Truth" for:
    *   `possibleActions(state, playerId)`: Generates valid moves **specific to the requesting player**. This includes:
        *   **Turn-based actions:** Moves allowed only when it is the player's turn (e.g., `PlayCard`).
        *   **State-independent actions:** Moves allowed anytime (e.g., `ReorderHand`, `LeaveGame`), if supported by the specific game rules.
    *   `reduce(state, action)`: Transitions the state.
    *   `validate(state, action)`: Validates incoming moves.
2.  **Versioning and Registry:** RuleSets will be named and versioned (e.g., `marias:v1`). A `RuleSetRegistry` will manage these, allowing different games to run simultaneously on different versions of rules.
3.  **Polymorphic State:** The `GameState` will be decoupled from specific game logic. Each `RuleSet` will work with its own specific state data (e.g., `MariasState`), likely using a polymorphic or generic approach.
4.  **API Integration:** The server will include `possibleActions` in the game state response, enabling "thin" clients.
5.  **UI Redesign:** The UI will transition to rendering its controls dynamically based on the provided actions (e.g., combo boxes for selections).

## Consequences

### Positive
*   **Thin Clients:** UI logic is significantly simplified. It only needs to know how to render an action and send its payload back.
*   **Single Source of Truth:** All game rules are strictly in the `engine` module.
*   **Guaranteed Consistency:** All clients (Web, Android) will always show the same valid moves.
*   **Better Testing:** We can unit test the "possibility generator" independently of any UI.

### Negative
*   **Payload Size:** The game state response will be slightly larger as it now contains the list of possible actions.
*   **Initial Refactoring:** Requires a shift in how current validation logic is structured (from "validate input" to "generate valid outputs").

## Alternatives Considered
*   **External Rule Engines (Drools/Easy Rules):** Rejected as they are too heavy, add unnecessary dependencies, and don't leverage Kotlin's type system as effectively as a custom implementation.
*   **JavaScript Rule Mirroring:** Rejected as it leads to "split-brain" bugs where the client and server disagree on rules.
