# F-016: End-to-End Testing

- Type: Feature
- Status: In Progress
- Source: Developer-driven quality assurance

## Summary
Implement a comprehensive E2E testing suite using Playwright to ensure the stability of the game's core loop, from creation to scoring.

## Description
As the complexity of the "Action Provider" pattern grows, manual verification becomes insufficient. This feature introduces automated browser-based tests that simulate real user interactions. These tests will cover critical paths such as creating a game, joining players, starting a match, and executing gameplay actions. It serves as the final gate to prevent regressions and UI crashes (like the "white screen" issue).

## Goals
- Automate the "Happy Path" of a Mariash game.
- Catch UI-only crashes and state initialization issues.
- Provide a reproducible environment for debugging reported bugs.
- Verify real-time updates (WebSocket/Polling) from a user perspective.

## Scope
- Playwright infrastructure setup in `ui/web`.
- Basic flow test: Create Game -> Navigate to Game Page.
- Multiplayer test: Join players -> Start Game -> Deal.
- Action-based gameplay verification.

## Tasks
- [x] T-051: Implement Playwright infrastructure and basic game flow test
- [ ] T-052: Mariash core loop E2E test (3 players)
