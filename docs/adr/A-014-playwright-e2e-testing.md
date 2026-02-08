# A-014 — Use Playwright for E2E Testing

- Status: Accepted
- Date: 2026-02-08

## Decision
Use Playwright as the primary framework for End-to-End (E2E) testing of the Web UI.

## Context
The project needs a way to verify the full user journey from game creation to gameplay, especially to catch issues like "white screens" or initialization race conditions that are hard to replicate in unit tests. Playwright offers robust browser automation, excellent debugging tools (Trace Viewer), and supports the modern web stack (Vite + React) used in this project.

## Consequences

Positive
- Fast and reliable browser automation.
- Auto-waiting features reduce test flakiness.
- Ability to test WebSocket and polling behaviors.
- Visual regression testing and trace viewing for debugging.

Trade-offs
- Adds another dependency and tool to the stack.
- E2E tests are slower than unit/integration tests.
