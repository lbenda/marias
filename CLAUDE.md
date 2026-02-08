# CLAUDE.md — AI Contract for this Repository

This file defines mandatory, stable rules for AI-assisted work in this repository.
It is intentionally short and low-entropy.

If information changes frequently, it does NOT belong here.

---

## 1) What this project is
A Kotlin-based card game engine (Mariash) focused on deterministic core logic,
clear domain modeling, and long-term maintainability.

The project prioritizes correctness, clarity, and low-context reasoning over
feature velocity or abstraction density.

---

## 2) Non-negotiable constraints
- Language: Kotlin
- Deterministic logic only (no randomness outside controlled inputs)
- No runtime reflection
- No UI layer
- No persistence beyond in-memory unless explicitly introduced
- No unnecessary dependencies

---

## 3) Architectural boundaries
- Core domain is pure and deterministic
- Infrastructure depends on domain, never the opposite
- Public behavior must be understandable from interfaces and small excerpts
- Prefer composition over deep hierarchies

Any architectural change requires an A in `docs/adr/`.

---

## 4) Token-friendly coding style
- Prefer clarity over cleverness
- Keep files and classes small and focused
- Avoid deep inheritance and excessive generics
- Do not introduce DSLs or heavy abstractions without strong justification
- Minimize surface area of public APIs

---

## 5) Interfaces as API contracts
Use Kotlin interfaces for:
- Cross-module or cross-package boundaries
- Public services and entry points
- Stable contracts frequently referenced in documentation or prompts

Do NOT introduce interfaces for:
- Local helpers
- One-off utilities
- Simple data transformations
- Classes not used outside their package

Interfaces are used as **context compression**, not for polymorphism by default.

---

## 6) Rules for changes
- Features are defined in `work/features/`
  - Features must include metadata: `- Type: Feature`, `- Status: ...`, `- Source: ...`
- Tasks and bugs live in `work/tasks/` and `work/bugs/`
- Always read the referenced task / bug before implementing
- Minimize changes outside the requested scope
- Preserve public APIs unless explicitly instructed otherwise

Progress tracking and status updates belong in the corresponding work item,
not in this file.

---

## 7) API and rules documentation
If you change:
- HTTP API → update `docs/API.md` and `docs/api-tests.http`
- Game mechanics → update the relevant file(s) under `docs/rules/`
  - Game rules are defined in ordered files under `docs/rules/` (01-*.md, 02-*.md, …).

Do not restate rules in tasks. Tasks and features must reference the exact rule files they rely on.

---

## 8) Safety boundaries
- Do not modify unrelated files
- Do not change formatting or lint rules unless asked
- Do not introduce new dependencies unless clearly justified or requested

