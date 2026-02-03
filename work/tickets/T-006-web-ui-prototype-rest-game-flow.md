# T-006: Web UI prototype — REST game flow (create/join/start/show hand)

- Status: Done
- Owner: ui/web
- Related modules: ui/web, server, engine
- Related ADRs: ADR-0002, ADR-0004, ADR-0006

## Goal
Create a minimal React UI that exercises the existing server REST API:
1) Create a new game
2) Join/sign in to a game (player identity)
3) Start the game
4) Display the player's deck/hand (whatever the API provides)

The goal is a fast, working vertical slice, not a polished UX.

## Context
- Web UI communicates only via server REST API.
- API is documented in docs/API.md and examples are in api-test.http.
- Server already provides baseline access to engine (create game, add players, shuffle/start, get state).

## Scope
IN:
- Simple React pages/components to support the flow:
    - Home / Create game
    - Join game (enter gameId + playerName or playerId)
    - Game view (start game + show player's cards)
- Minimal API client wrapper for REST calls
- Basic loading/error states
- Use one consistent state model in UI (simple reducer/state container is fine)

OUT:
- Auth, security hardening
- Persistence / reconnect / multi-session handling
- Styling polish, animations
- Full gameplay actions beyond the flow above

## Assumptions
- The server exposes endpoints needed for:
    - create game
    - add/join player
    - start/shuffle/init game
    - fetch game state and/or player-specific view (hand/deck)
      If any endpoint is missing or response does not contain enough info to render "player deck/hand", adjust server API minimally and update docs/API.md + api-test.http accordingly.

## Implementation plan (follow this)
1) Read PROJECT_CONTEXT.md and ui/web folder structure.
2) Read docs/API.md and api-test.http to identify exact endpoints + payloads.
3) Define a minimal UI routing:
    - / (create game)
    - /join (join existing game)
    - /game/:gameId (game screen)
4) Implement a small API client module:
    - typed request/response helpers
    - base URL config (env var or simple constant)
5) Implement screens:
    - Create Game: button → call create → navigate to /game/:id (or show id and link)
    - Join Game: form (gameId + name) → call join/add player → navigate
    - Game View:
        - fetch state on load
        - Start game button (if needed) → call start/shuffle/init
        - render player's cards (hand/deck) from response
6) Add minimal error handling:
    - show error message if request fails
    - show loading indicator while fetching
7) Document any required API adjustments:
    - If server changes were needed: update docs/API.md and api-test.http.

## Definition of Done
- A developer can:
    - create a game from the web UI
    - join as a player
    - start the game (or initialize/shuffle if that is the server behavior)
    - see the player's deck/hand rendered in the UI
- No unrelated refactors.
- If API changed: docs/API.md + api-test.http updated.

## Notes
- Prefer simple components and minimal state. Avoid over-architecting.
- Keep UI and engine separated: web UI talks only to REST.

### UI requirements: displaying player hand (cards)
- Display cards as a simple responsive grid of "card tiles" (no images).
- Each tile shows a compact label:
    - `rank` + `suit` (e.g. `A♠`, `10♥`, `K♦`, `7♣`)
- Tile styling (minimal):
    - fixed size (approx 56x80 px)
    - border + rounded corners
    - centered text
- Do not add external UI libraries or card asset packs.
- If the API provides suit/rank in a different shape, map it locally in UI.
- Implement a small mapping helper to convert server card representation into a display label.
 