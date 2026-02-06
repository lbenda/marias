# T-033: Implement GitHub Actions ticket sync (Markdown → Issue → Project v2)

* Type: Task
* Status: Done 
* Source: F-012

## Goal
Implement the automation that keeps GitHub Issues and GitHub Project (v2) in sync with ticket Markdown files stored in the repository.

The repository is the source of truth. GitHub is a status mirror.

## Scope
### In scope
- Workflows:
  - `.github/workflows/tickets-sync.yml` (on push + PR)
  - `.github/workflows/tickets-on-merge.yml` (on PR merged to master)
- Scripts (Node.js):
  - `.github/scripts/tickets-sync.js`
  - `.github/scripts/tickets-on-merge.js`
  - `.github/scripts/projectv2.js` (GraphQL helper)
- Ticket parsing (from Markdown):
  - H1 title
  - `Status:` line
  - ticket ID (from filename or H1 prefix)
- Issue mapping:
  - create issue if missing
  - update title/body/labels if exists
  - close on merge and set status to `Merged`
- Project v2 mapping:
  - ensure issue is added to the Project
  - set Project fields: `Status`, `Area`

### Out of scope
- Local CLI tooling
- Two-way sync (GitHub → repo)
- Complex dependency graphs / reporting

## Inputs
- Ticket files under:
  - `project/work/bugs/**`
  - `project/work/features/**`
  - `project/work/tasks/**`

Ticket minimal format:
- `# <ID>: <Title>`
- `- Status: <Value>`

## Outputs
- GitHub Issue:
  - Title = H1
  - Body = link to file in `master`
  - Labels:
    - `ticket:<ID>`
    - `area:bugs|features|tasks`
    - `status:<Status>`
- GitHub Project v2:
  - Status field set to the same value as in Markdown
  - Area field derived from directory

## Status model
- Markdown Status values must match Project v2 Status options (case-sensitive).
- Issue state mapping:
  - `Merged` ⇒ closed
  - otherwise ⇒ open
- On merge to `master`:
  - Issue closed
  - Issue label set to `status:Merged`
  - Project Status set to `Merged`
  - Ticket file updated: `Status: Done` → `Status: Merged`

## Definition of Done
- Changing a ticket file triggers workflow and:
  - creates issue if none exists for `ticket:<ID>`
  - updates issue labels on Status change
  - adds/updates issue in Project v2 with correct Status/Area
- Merging a PR to `master`:
  - closes the issue(s) for affected tickets
  - updates Project status to `Merged`
  - commits the Done→Merged change back to `master`
- Workflows are idempotent (re-running produces same end state).
- No secrets are committed in the repository.

## Implementation Notes
- Use `secrets.GITHUB_TOKEN` for Issues/labels.
- Use `secrets.PROJECT_TOKEN` (PAT classic or GitHub App token) for Project v2 GraphQL calls.
- Labels should be created automatically if missing.
- When setting Project fields, warn (do not fail) if an option is not found.

## Related
- F-012: DevOps Tooling & Project Synchronization
- T-034: Project v2 admin setup
