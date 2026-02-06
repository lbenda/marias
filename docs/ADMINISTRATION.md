# Administration

## 1️⃣ GitHub Project (v2)

### Project Creation
Create a **GitHub Project (v2)**:
- either **User project**
- or **Organization project**

Remember:
- **OWNER** (user or organization)
- **PROJECT NUMBER** (project number in URL)

Example URL: `https://github.com/orgs/my-org/projects/5`

Change configuration in [tickets-one-merge.yml](../.github/workflows/tickets-one-merge.yml), [tickets-sync.yml](../.github/workflows/tickets-sync.yml), 
→ `PROJECT_KIND=organization`
→ `PROJECT_OWNER=my-org`
→ `PROJECT_NUMBER=5`

---

### Required Fields in Project

The project must have the following fields:

#### 🟦 Status (Single select)
Must contain the **same values** used in Markdown files:

- Todo
- In Progress
- Done
- Merged
- Blocked
  *(or others as needed)*

> Values are **case-sensitive** – they must match exactly.

---

#### 🟩 Area (Single select)

Recommended values:
- bugs
- features
- tasks

These values correspond to file locations:

```
work/bugs/
work/features/
work/tasks/
```

---

## 2️⃣ GitHub Personal Access Token (PAT) for Projects v2

GitHub Actions **cannot use `GITHUB_TOKEN` for Projects v2**.
You must create a **Personal Access Token (classic)**.

### Token Creation
1. GitHub → **Settings**
2. **Developer settings**
3. **Personal access tokens**
4. **Tokens (classic)** → *Generate new token*

### Minimum Permissions
Check:
- ✅ `project`
- ✅ `repo` *(required for working with issues in private repositories)*

For **organization projects** you may also need:
- ✅ `read:org`

---

### Adding Token to Repository
In the repository:
1. **Settings**
2. **Secrets and variables → Actions**
3. **New repository secret**

Name: `PROJECT_TOKEN`
Value: `<your PAT token>`

---

## 3️⃣ GitHub Actions Configuration

Workflow files are located in: `.github/workflows/`


### Required Variables in Workflow

The following variables must be correctly set in workflow files:

```yaml
PROJECT_KIND: organization   # or "user"
PROJECT_OWNER: my-org        # organization or user name
PROJECT_NUMBER: "5"          # Project v2 number

PROJECT_FIELD_STATUS: Status
PROJECT_FIELD_AREA: Area
```

---

## 4️⃣ Workflow Permissions

Workflows use these permissions:

Automatically set

```yaml
permissions:
  contents: read | write
  issues: write
```

__Access to Project v2__

Handled via:

PROJECT_TOKEN (PAT)
