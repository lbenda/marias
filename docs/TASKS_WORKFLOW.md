# Tasks Workflow — AI-first task management

This document describes how work is planned, executed, and tracked in this repository
using Markdown files (features and tasks).
The workflow is designed for human + AI (Claude) collaboration.

Tasks are not documentation or architecture records.
Tasks describe *work*.

---

## 1. Purpose of Markdown tasks

Work is split into two levels:
- **features** (work/features/) — high-level business description of system features
- **tasks** (work/tasks/) — splitting of features into implementation details and work items

Markdown tasks serve as:
- the source of truth for active and planned work
- a stable, reusable prompt for AI
- a scope and workflow guard (prevents uncontrolled changes)

A good task answers:
- what should be done
- why it is needed now
- what is explicitly out of scope
- how the work should proceed
- when the work is considered done

---

## 2. Where tasks live

```
/work/
  features/
    F-XXX-*.md
  tasks/
    T-XXX-*.md
```

Separation of concerns:
- `docs/` — stable knowledge (API, ADRs, guidelines)
- `PROJECT_CONTEXT.md` — current system map (what exists)
- `CLAUDE.md` — rules for AI behavior
- `work/features/` — feature definitions (split into tasks for implementation)
- `work/tasks/` — active and historical work items

Tasks are intentionally NOT placed under `docs/`.

---

## 3. Naming convention

Features:
```
F-<number>-<short-kebab-description>.md
```

Tasks:
```
T-<number>-<short-kebab-description>.md
```

Examples:
- `F-001-two-phase-dealing.md`
- `T-006-web-ui-prototype-rest-game-flow.md`
- `T-007-engine-action-validation.md`

- Number represents backlog order.
- Name describes *what*, not *how*.
- Tasks may reference a parent feature.

---

## 4. Task lifecycle

Typical statuses:
- `Planned` — idea exists, not ready to work on
- `Ready` — fully specified, can be implemented
- `In Progress` — currently being worked on
- `Done` — completed and verified

After `Done`, tasks remain as historical records.

---

## 5. Standard task structure

Every task should follow this structure:

```md
# T-XXX: Short title

- Status: Planned | Ready | In Progress | Done
- Owner: module / team / AI
- Parent: F-XXX (optional)
- Related modules: engine, server, ui/web
- Related ADRs: ADR-000X, ADR-000Y

## Goal
One or two sentences describing the intended outcome.

## Context
Why this work is needed now.
Reference existing system state, not architectural rationale.

## Scope
IN:
- Explicit list of what is included

OUT:
- Explicit list of what must NOT be done

## Constraints
- Must follow CLAUDE.md rules
- Must respect PROJECT_CONTEXT.md
- Any additional constraints (no new deps, no server changes, etc.)

## Implementation plan
Concrete, ordered steps (3–8 bullets).
AI must follow this plan and not invent a new workflow.

## Definition of Done
Objective criteria:
- what must work
- what must be updated (docs/API.md, tests, etc.)

## Notes (optional)
Clarifications, warnings, or trade-offs.
```
