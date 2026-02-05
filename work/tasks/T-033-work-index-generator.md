# T-033: Work Index Generator Script

- Parent: F-012
- Status: Planned
- Owner: devops
- Related modules: scripts
- Depends on: none

## Goal
Create Node.js script that generates `work/WORK_INDEX.md` with Kanban-style
overview of all features and tasks.

## Directory Structure
```
scripts/
  package.json
  tsconfig.json
  src/
    work-index.ts
    utils/
      markdown.ts
      task-parser.ts
    types/
      index.ts
  dist/
```

## Task Parser

### Parsing Task/Feature Files
Extract from markdown frontmatter-style headers:
```markdown
# T-028: Server - Polling Endpoint

- Parent: F-011
- Status: Planned
- Owner: server
- Depends on: T-030
```

### Data Structure
```typescript
interface WorkItem {
  id: string;           // T-028, F-011
  type: 'task' | 'feature';
  title: string;
  status: 'Planned' | 'In Progress' | 'Done';
  owner?: string;
  parent?: string;      // Feature ID for tasks
  dependsOn?: string[]; // Task/feature IDs
  filePath: string;
}
```

## Index Generator

### Grouping
1. Group by status
2. Within status, group features first, then tasks
3. Sort by ID within groups

### Output Format
```markdown
# Work Index

*Generated: 2026-02-05 14:30*

## Summary
- Features: 5 planned, 2 in progress, 3 done
- Tasks: 12 planned, 3 in progress, 15 done

## In Progress

### Features
| ID | Title | Tasks |
|----|-------|-------|
| [F-007](features/F-007-two-phase-dealing.md) | Two-phase dealing | 3/7 |

### Tasks
| ID | Title | Owner | Parent | Blocked By |
|----|-------|-------|--------|------------|
| [T-028](tasks/T-028-server-polling-endpoint.md) | Polling endpoint | server | F-011 | |

## Planned
...

## Done
| ID | Title | Owner | Completed |
|----|-------|-------|-----------|
| [T-024](tasks/T-024-implement-vocabulary-in-code.md) | Implement vocabulary | engine | 2026-02-05 |
```

## Archive Done Items (Optional)

### Flag
`--archive` - Move done items to archive subdirectory

### Structure
```
work/
  tasks/
    T-028-...md (active)
  features/
    F-011-...md (active)
  archive/
    tasks/
      T-024-...md (done)
    features/
      F-008-...md (done)
```

### Behavior
- Move files with `Status: Done` to archive
- Update links in WORK_INDEX.md accordingly
- Keep archive items in Done section

## CLI Interface
```bash
# Generate index only
npm run work-index

# Generate and archive done items
npm run work-index -- --archive

# Verbose output
npm run work-index -- --verbose
```

## package.json
```json
{
  "name": "marias-scripts",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "build": "tsc",
    "work-index": "node dist/work-index.js",
    "work-index:dev": "ts-node src/work-index.ts"
  },
  "devDependencies": {
    "typescript": "^5.0.0",
    "@types/node": "^20.0.0",
    "ts-node": "^10.0.0"
  }
}
```

## Files to Create
- `scripts/package.json`
- `scripts/tsconfig.json`
- `scripts/src/work-index.ts`
- `scripts/src/utils/markdown.ts`
- `scripts/src/utils/task-parser.ts`
- `scripts/src/types/index.ts`

## Definition of Done
- Script parses all task/feature files correctly
- Generates accurate WORK_INDEX.md
- Groups by status with correct counts
- Links to source files work
- Archive flag moves done items
- Runs without errors on current work/ content
