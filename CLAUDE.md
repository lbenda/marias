# CLAUDE.md — AI coding rules for this repository

This file defines mandatory rules for AI-assisted work in this repository.
Project structure, architecture, and current system shape are defined in PROJECT_CONTEXT.md.
Architectural rationale and history are documented in docs/adr/.

---

## 1) Primary objectives
- Produce correct, maintainable code with minimal complexity.
- Optimize for low-context usage: public behavior must be understandable from interfaces and small excerpts.
- Prefer clarity over cleverness.

---

## 2) Context sources (priority order)
1. Closest CLAUDE.md (package/module level overrides root)
2. PROJECT_CONTEXT.md (current system map)
3. Explicit user instructions
4. Code (only the minimum required)

Do NOT read or rely on ADR files unless explicitly asked or when proposing architectural changes.

---

## 3) Token-friendly coding style
- Reduce verbosity wherever possible without harming clarity.
- Remove redundant abstractions.
- Flatten deep class hierarchies.
- Minimize generics, DSLs, and heavy type-level programming.
- Prefer simple data structures and straightforward control flow.
- Keep files and classes small and focused.

---

## 4) Interfaces as API contracts
- Prefer Kotlin interfaces for:
  - Cross-module and cross-package boundaries
  - Public services and entry points
  - Components frequently referenced in prompts or documentation
- Interfaces are used as:
  - Stable API contracts
  - Context compression for AI tooling
- Interfaces are NOT introduced primarily for polymorphism.

Do NOT generate interfaces for:
- Local helpers
- One-off utilities
- Simple data transformations
- Classes not referenced outside their package

---

## 5) Documentation split
- Human-facing documentation lives in README, KDoc, and docs/.
- AI context must rely on PROJECT_CONTEXT.md and local CLAUDE.md files.
- Avoid copying large comment blocks or documentation into prompts.

---

## 6) Workflow rules (always follow)

Before making changes:
1. Identify the target module/package.
2. Read PROJECT_CONTEXT.md.
3. Read the closest CLAUDE.md.
4. If working in a new or complex area, create a local CLAUDE.md first.

When implementing:
- Start with a short plan (3–8 bullets).
- Minimize changes outside the requested scope.
- Preserve public APIs and behavior unless explicitly told otherwise.

After implementing:
- Change the ticket:
  - Set ticket Status to Done
  - Summarize what changed and why (5–10 bullets).
  - Add a short "Result" and "Verification" section if missing
- If an architectural decision is introduced or changed:
  - add or update an ADR in docs/adr/
  - reflect the resulting state in PROJECT_CONTEXT.md
- If the HTTP API changed, update `docs/API.md` and `docs/api-test.http`

### Work tracking
- Active work is defined and tracked in Markdown tickets under /work/tickets/.
- For any task, read the referenced ticket and write progress updates into that ticket.
- Always read the referenced ticket before starting implementation.
- Follow the ticket workflow and constraints exactly.
- Do NOT write step-by-step progress into IMPLEMENTATION_PROGRESS.md.
- IMPLEMENTATION_PROGRESS.md is used only for occasional milestone summaries (when explicitly requested).

---

## 7) API changes
If you change anything in the HTTP API (endpoints, payloads, status codes, auth, error format), you MUST update:
- docs/API.md
- api-test.http

Treat these files as part of the API contract.

---

## 8) Safety boundaries
- Do not introduce new dependencies unless requested or clearly justified.
- Do not change formatting or lint rules unless explicitly asked.
- Do not modify unrelated files.
