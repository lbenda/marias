# F-012: DevOps Tooling

* Type: Feature
* Status: Planned
* Source: Project management needs

## Description
Create DevOps scripts and tooling for project management automation:
- Work index generator (Kanban overview of tasks/features)
- Future: GitHub Actions integration
- Future: Additional automation scripts

## Directory Structure
```
scripts/
  package.json           # Node.js dependencies
  tsconfig.json          # TypeScript config
  src/
    work-index.ts        # Generate WORK_INDEX.md
    utils/
      markdown.ts        # Markdown parsing/generation
      task-parser.ts     # Parse task/feature files
  dist/                  # Compiled JavaScript
```

## Work Index Generator

### Input
- `work/features/F-*.md` - Feature files
- `work/tasks/T-*.md` - Task files

### Output
- `work/WORK_INDEX.md` - Kanban-style overview

### Features
- Parse status from task/feature files
- Group by status (Planned, In Progress, Done)
- Show dependencies between tasks
- Optionally archive done items to subdirectory

### Example Output
```markdown
# Work Index

## In Progress
| ID | Subject | Owner | Blocked By |
|----|---------|-------|------------|
| F-007 | Two-phase dealing | engine | |
| T-028 | Polling endpoint | server | |

## Planned
| ID | Subject | Owner | Blocked By |
|----|---------|-------|------------|
| T-029 | WebSocket endpoint | server | T-030 |

## Done
| ID | Subject | Completed |
|----|---------|-----------|
| T-024 | Implement vocabulary | 2026-02-05 |
```

## Integration

### Gradle Task
```kotlin
tasks.register<Exec>("generateWorkIndex") {
    workingDir = file("scripts")
    commandLine("npm", "run", "work-index")
}
```

### Future: GitHub Actions
- Run on PR to update index
- Validate task file format
- Check for stale tasks

## Success Criteria
- Script generates accurate WORK_INDEX.md
- Callable from Gradle
- Easy to extend with new scripts
- TypeScript for type safety

## Related Tasks
- T-033: Work index generator script
- T-034: Gradle integration for scripts
