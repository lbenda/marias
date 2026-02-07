# F-012: DevOps Tooling & Project Synchronization

* Type: Feature
* Status: Merged
* Source: Repository-driven project management

## Description

Create DevOps tooling that treats the **repository as the single source of truth**
and automatically synchronizes project state into GitHub (Issues + Projects v2).

The goal is **not** to manage work inside GitHub,
but to use GitHub as a **read-only visualization and status mirror**
of what is defined in Markdown files stored in the repository.

This feature establishes the foundation for:
- automated issue lifecycle
- project-wide status overview
- future tooling (indexes, validation, reporting)

## Core Principles

- 📄 **Repository is the source of truth**
- 🔁 Synchronization is **one-way**: `Markdown → GitHub Issue → GitHub Project (v2)`

- ❌ No manual state changes in GitHub
- 🧠 Markdown files contain full ticket context
- 📊 GitHub shows only project-level state

## Scope

### Included
- GitHub Actions for synchronizing tickets
- Status propagation from Markdown to:
- Issue state (`open` / `closed`)
- Issue labels
- Project v2 fields
- Merge-driven lifecycle (Done → Merged)
- Deterministic, idempotent automation

### Explicitly Excluded
- Manual editing of issues or projects
- GitHub Projects as a source of truth
- Two-way synchronization
- Jira-like workflow complexity

## Ticket Format (Source of Truth)

Tickets are stored as Markdown files under:

```
/work/
  bugs/
  features/
  tasks/
```

Minimal required structure:

```markdown
# F-012: DevOps Tooling & Project Synchronization

- Status: Todo
```

Rules:
- Ticket ID must be present in filename or H1
- Status: must match Project v2 Status options
- All metadata lives in the file

## Status Model
### Canonical Statuses (defined in Markdown)

- Todo
- In Progress
- Done
- Merged
- Blocked

### Mapping

| Markdown Status | GitHub Issue | GitHub Project       |
|-----------------| ------------ |----------------------|
| Todo            | open         | Status = Todo        |
| In Progress     | open         | Status = In Progress |
| Done            | open         | Status = Done        |
| Merged          | closed       | Status = Merged      |
| Blocked         | open         | Status = Blocked     |

Issues are closed only on merge, never earlier.

## Automation Behavior
### On Push / PR
- Parse changed ticket files
- Create or update GitHub Issue
- Apply labels:
  - ticket:F-012
  - area:features
  - status:Todo (etc.)
- Add issue to Project v2 (if missing)
- Synchronize Project fields:
  - Status
  - Area

## On Merge to _master_

- Close related GitHub Issue
- Set Issue label status:Merged
- Update Project v2:
  - Status → Merged
- Update Markdown: `Status: Done → Status: Merged`

## Tooling Structure

```
.github/
  workflows/
    tickets-sync.yml        # Markdown → Issue → Project
    tickets-on-merge.yml   # Merge lifecycle handling
scripts/
  tickets-sync.js
  tickets-on-merge.js
  projectv2.js            # Project v2 GraphQL helper
```

No runtime tooling is required locally.
All synchronization is performed via GitHub Actions.

## Project Visualization
GitHub Project (v2) is used exclusively as a dashboard.

Required fields:
  - Status (single select)
  - Area (single select)

The Project must be treated as read-only:
manual edits will be overwritten by automation.

## Future Extensions

This feature intentionally lays groundwork for:

- 📊 Generated WORK_INDEX.md (static overview)
- ✅ Ticket schema validation
- 🧹 Stale ticket detection
- 🧩 Dependency visualization
- 🔄 Multi-repo aggregation (optional)

All future tooling must follow the same principle: __repository first, GitHub second.__

## Success Criteria
- Tickets are fully manageable from Markdown
- GitHub accurately reflects project state
- No manual project management needed in GitHub
- Automation is predictable and repeatable
- System scales from PoC to multi-repo without redesign

## Related Work
- T-033: Implement GitHub Actions ticket sync (Markdown → Issue → Project v2)
- T-034: Set up GitHub Project v2 + tokens for automation
