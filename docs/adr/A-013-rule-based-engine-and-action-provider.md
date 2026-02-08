# A-013: Rule-Based Engine and Action Provider

- Status: Proposed

## Context
The current game logic is split between validation in the engine (`GameRules.validate`) and action preparation in the UI. This leads to:
1.  **Duplicated Logic:** The UI needs to "guess" which actions are valid to show the correct buttons or highlight playable cards.
2.  **Increased UI Complexity:** The React/JS layer must understand game rules to provide a good user experience.
3.  **Maintenance Burden:** Adding a new game rule or action requires changes in both the Kotlin engine and all UI clients.

We need a way to centralize all game possibilities in the engine so the UI can be purely "data-driven."

## Decision
We will implement a **Custom Rule-Based Engine** using an **Action Provider** pattern, designed for **Multi-Game Support**.

Key technical details:
1.  **RuleSet as Transformation Machine:** Each game type is a `RuleSet` that acts as a pure transformation function: `(State, Action) -> State`.
    *   **Action-in-State:** The `GameState` contains both the `history` of executed actions and the current `possibleActions` for all players.
    *   **Automatic Chaining:** A single player action can trigger a chain of automatic engine actions (e.g., dealer auto-deals, trick auto-clears). The `RuleSet` is responsible for processing these until a state is reached where a human player must act.
    *   **Single Source of Truth:** The `RuleSet` provides:
        *   `possibleActions(state, playerId)`: To generate the "menu" for the next state.
        *   `reduce(state, action)`: To calculate the next state.
        *   `validate(state, action)`: To ensure the transformation is legal.
2.  **Versioning and Registry:** RuleSets will be named and versioned (e.g., `marias:v1`). A `RuleSetRegistry` will manage these, allowing different games to run simultaneously on different versions of rules.
3.  **Universal State Model:**
    *   **Aggregated Actions:** The engine provides high-level, semantic actions (e.g., `ShuffleDeck`, `PlayCard`, `SelectTalon`) as `possibleActions`. The UI interacts with these aggregated intents rather than atomic moves.
    *   **Atomic Move Philosophy:** Internally, the `RuleSet` reduces these aggregated actions into a sequence of **Atomic Moves** (card path changes) and **Visibility Changes**.
    *   **Universal Card Map:** Every state contains a `Map<Card, CardPosition>`, where `CardPosition` includes `path` (e.g., `player[0]/hand[5]`) and `visibility` (List<PlayerId>).
    *   **Polymorphic Metadata:** Games can define their own non-card state (e.g., money in bank, bidding points, trump suit) using a polymorphic `metadata` object within the `GameState`.
    *   **Generic Actions:** `GameAction` is a sealed class that includes:
        *   **Card Actions:** `PlayCard`, `MoveCard`, `ExchangeTalon`.
        *   **Meta Actions:** `PlaceBid`, `DeclareMarriage`, `SetTrump`.
        *   **System Actions:** `ConnectPlayer`, `DisconnectPlayer`, `LeaveGame`.
    *   **RuleSet Role:** The `RuleSet` is responsible for generating valid actions and reducing them into updates for both the Card Map and the Metadata.
4.  **API Integration:** The server will include `possibleActions` in the game state response, enabling "thin" clients.
5.  **UI Redesign:** The UI will transition to rendering its controls dynamically based on the provided actions (e.g., combo boxes for selections).

## Consequences

### Positive
-   **Thin Clients:** UI logic is significantly simplified. It only needs to know how to render an action and send its payload back.
-   **Single Source of Truth:** All game rules are strictly in the `engine` module.
-   **Guaranteed Consistency:** All clients (Web, Android) will always show the same valid moves.
-   **Better Testing:** We can unit test the "possibility generator" independently of any UI.

### Negative
-   **Payload Size:** The game state response will be slightly larger as it now contains the list of possible actions.
-   **Initial Refactoring:** Requires a shift in how current validation logic is structured (from "validate input" to "generate valid outputs").

## Alternatives Considered
-   **External Rule Engines (Drools/Easy Rules):** Rejected as they are too heavy, add unnecessary dependencies, and don't leverage Kotlin's type system as effectively as a custom implementation.
-   **JavaScript Rule Mirroring:** Rejected as it leads to "split-brain" bugs where the client and server disagree on rules.
