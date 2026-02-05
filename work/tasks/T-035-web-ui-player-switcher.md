# T-035: Web UI - Player Switcher

- Parent: F-007
- Status: Planned
- Owner: web
- Related modules: ui/web
- Depends on: T-012

## Goal
Add ability to switch between players in the web UI for testing and debugging purposes.

## Context
Currently the UI stores a single `playerId` per game in localStorage when joining.
There's no way to view the game from different players' perspectives or play as
different players in the same browser session.

## Scope

IN:
- Player dropdown/selector showing all players in the game
- Switch active player without rejoining
- See selected player's hand
- Act as selected player (make decisions, play cards)
- Visual indicator of currently selected player

OUT:
- Multi-window/tab synchronization
- Spectator mode (non-player view)
- Player authentication

## Implementation Notes
- Add player selector dropdown to GamePage header
- Store selected playerId in component state (not localStorage for flexibility)
- Keep localStorage playerId as "default" player on page load
- Update hand loading and action dispatch to use selected player

## Definition of Done
- Dropdown shows all players in the game
- Selecting a player updates the hand display
- Actions are dispatched as the selected player
- Works correctly during all game phases
- Default to localStorage playerId on initial load
