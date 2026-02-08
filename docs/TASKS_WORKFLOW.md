# Task Workflow

This document describes how work items are used in this repository.
It is for humans, not for AI execution.

## Work item types

### Feature (F-xxx)
Represents a user-visible or domain-level capability.
A feature:
- defines intent and scope
- references relevant rules (R-xxx)
- is implemented via one or more tasks
- **Must include**: `- Status:` with value: `Todo`, `In Progress`, `Done`, `Merged`, or `Blocked`

### Task (T-xxx)
Represents a concrete unit of implementation work.
A task:
- has a clear goal
- references a feature or rule
- includes definition of done
- **Must include**: `- Status:` with value: `Todo`, `In Progress`, `Done`, `Merged`, or `Blocked`

### Bug (B-xxx)
Represents incorrect behavior relative to rules or features.
A bug:
- documents steps to reproduce
- describes actual vs expected behavior
- **Must include**: `- Status:` with value: `Todo`, `In Progress`, `Done`, `Merged`, or `Blocked`

### Architectonic decision (A-xxx)
Records a structural or architectural decision.
Any change affecting multiple components or rules requires an architectonic decision.
ADRs have different status values: `Proposed`, `Accepted`, `Rejected`, `Superseded`, etc.

## Status Field Requirements

All work items (Features, Tasks, Bugs) **must** include a `- Status:` field in their front matter.

### Valid Status Values

**For Features (F-xxx), Tasks (T-xxx), and Bugs (B-xxx):**
- `Todo` - Not yet started
- `In Progress` - Currently being worked on
- `Done` - Implementation complete, awaiting review/merge
- `Merged` - Merged into main branch
- `Blocked` - Cannot proceed due to dependency or blocker

**For Architectonic Decisions (A-xxx):**
- `Proposed` - Under consideration
- `Accepted` - Approved and in effect
- `Rejected` - Not adopted
- `Superseded` - Replaced by a newer decision
- `Deprecated` - No longer recommended

### Example
```markdown
# T-051: RuleSet extension for contract selection

- Type: Task
- Status: Todo
- Feature: F-016
```

The `scripts/check-work-items.sh` script validates that all work items have a valid status.

## Rules
- Tasks must not restate rules
- Features must reference relevant rules
- Progress tracking belongs in work items, not global docs
- All B/F/T items must have a valid Status field
