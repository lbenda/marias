# T-051: Implement Playwright infrastructure and basic game flow test

- Parent: F-016
- Status: Merged
- Owner: ui
- Related modules: ui/web

## Summary
Setup Playwright in the `ui/web` module and create a foundational test that verifies a player can create a game and see the game page without errors.

## Goal
Establish a reliable E2E test environment that can detect "white screen" issues during the initial game page load.

## Scope
- [x] Add `@playwright/test` to `ui/web` devDependencies.
- [x] Configure `playwright.config.ts` (base URL, web server, etc.).
- [x] Implement `tests/game-flow.spec.ts`:
    - [x] Navigate to `/`
    - [x] Enter player name
    - [x] Click "Create Game"
    - [x] Wait for redirection to `/game/{id}`
    - [x] Verify "Loading game..." is gone and game details are visible.
- [x] Ensure tests can run against a locally started server and frontend.

## Files to Create/Modify
- `ui/web/package.json` (modify)
- `ui/web/playwright.config.ts` (new)
- `ui/web/tests/game-flow.spec.ts` (new)

## Definition of Done
- Playwright is installed and configured.
- `npm run test:e2e` executes tests successfully.
- A test exists that reproduces the reported "white screen" issue (or verifies it's fixed).
- Test passes in headless mode.
