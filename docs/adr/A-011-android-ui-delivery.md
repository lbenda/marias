# Ax-011: Android UI Delivery: WebView vs Native UI (Pending)

- Status: Proposed (Pending)
- Date: 2026-02-02

## Decision
The Android UI approach is currently undecided:

Option A: Embed the React UI in an Android WebView
Option B: Build a native Android UI (e.g., Jetpack Compose)

## Context

We want to maximize development speed while keeping the engine consistent. WebView would reuse the React UI, while native UI would provide a more platform-native experience and potentially better performance and integrations.

## Consequences (if Option A: WebView)

Positive
- Reuse UI code and logic from the web.
- Faster to ship Android UI initially.

Trade-offs
- WebView constraints, platform quirks, performance considerations.
- Bridging between Kotlin engine and JS UI requires a stable interface and careful security model.

## Consequences (if Option B: Native)

Positive
- Best Android UX, performance, and platform integration.
- Cleaner direct integration with Kotlin engine.

Trade-offs
- Separate UI codebase from web, higher maintenance cost.

## Follow-up
Decide after defining:

- required platform integrations (notifications, offline, persistence)
- performance/animation expectations
- preferred architecture boundary (shared engine vs API)
