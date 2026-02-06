# T-034: Set up GitHub Project v2 + tokens for automation

* Type: Task
* Status: Done
* Source: F-012

## Goal
Prepare GitHub configuration so that ticket synchronization workflows can run reliably:
- GitHub Project (v2) exists with required fields/options
- Required tokens/secrets are created and stored
- Repository Actions permissions are configured

## Scope
- Project v2 setup
- PAT tokens

## Steps

### 1) Create a Project (v2)
Create a Project (v2) under:
- User (recommended for PoC/solo dev), or
- Organization (if repo is owned by an org)

Record values from the Project URL:
- PROJECT_KIND = `user` or `organization`
- PROJECT_OWNER = username or org name
- PROJECT_NUMBER = project number from URL

Example:
`https://github.com/users/<user>/projects/5`
- PROJECT_KIND=user
- PROJECT_OWNER=<user>
- PROJECT_NUMBER=5

### 2) Configure Project fields
In the Project, create fields:

#### Field: Status (single select)
Add options that match repository tickets (case-sensitive), e.g.:
- Todo
- In Progress
- Done
- Merged
- Blocked

#### Field: Area (single select)
Add options:
- bugs
- features
- tasks

### 3) Create PROJECT_TOKEN (PAT classic)
GitHub Actions `GITHUB_TOKEN` is not sufficient for Project v2 API operations.
Create a Personal Access Token (classic) with:
- `project`
- `repo` (recommended; required for private repos)
- `read:org` (only if using an org project and required by your org settings)

Store it as a repository secret:
- Name: `PROJECT_TOKEN`
- Value: the PAT token

### 4) Confirm workflow permissions
In the repository:
Settings → Actions → General → Workflow permissions:
- Read and write permissions
- Allow GitHub Actions to create and approve pull requests

### 5) Set workflow env config
In workflows, set:
- PROJECT_KIND
- PROJECT_OWNER
- PROJECT_NUMBER
- PROJECT_FIELD_STATUS (default: `Status`)
- PROJECT_FIELD_AREA (default: `Area`)

### 6) Validation checklist
- A push that changes a ticket file:
  - creates/updates a GitHub Issue
  - adds/updates the item in Project v2
  - sets Status + Area fields correctly
- A merged PR to master:
  - closes issue(s)
  - sets Status=Merged in Project
  - updates `Status: Done` → `Status: Merged` in ticket file and commits to master

## Definition of Done
- All required secrets are present (`PROJECT_TOKEN`)
- Project fields exist and contain matching options
- Workflows can write issues and update Project fields without permission errors

## Related
- F-012: DevOps Tooling & Project Synchronization
- T-033: Implement GitHub Actions ticket sync
