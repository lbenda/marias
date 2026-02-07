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

### Task (T-xxx)
Represents a concrete unit of implementation work.
A task:
- has a clear goal
- references a feature or rule
- includes definition of done

### Bug (B-xxx)
Represents incorrect behavior relative to rules or features.

### Architectonic decision (A-xxx)
Records a structural or architectural decision.
Any change affecting multiple components or rules requires an architectonic decision.

## Rules
- Tasks must not restate rules
- Features must reference relevant rules
- Progress tracking belongs in work items, not global docs
