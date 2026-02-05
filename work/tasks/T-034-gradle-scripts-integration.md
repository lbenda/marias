# T-034: Gradle Integration for Scripts

- Parent: F-012
- Status: Planned
- Owner: devops
- Related modules: scripts, build
- Depends on: T-033

## Goal
Integrate Node.js scripts with Gradle build system for easy execution
and future CI/CD pipeline integration.

## Gradle Tasks

### generateWorkIndex
```kotlin
tasks.register<Exec>("generateWorkIndex") {
    group = "documentation"
    description = "Generate work/WORK_INDEX.md from task and feature files"

    workingDir = file("scripts")
    commandLine("npm", "run", "work-index")

    // Ensure npm install has been run
    dependsOn("npmInstall")
}
```

### npmInstall
```kotlin
tasks.register<Exec>("npmInstall") {
    group = "build setup"
    description = "Install npm dependencies for scripts"

    workingDir = file("scripts")
    commandLine("npm", "install")

    // Only run if node_modules doesn't exist or package.json changed
    inputs.file("scripts/package.json")
    outputs.dir("scripts/node_modules")
}
```

### archiveDoneTasks
```kotlin
tasks.register<Exec>("archiveDoneTasks") {
    group = "documentation"
    description = "Archive completed tasks and features"

    workingDir = file("scripts")
    commandLine("npm", "run", "work-index", "--", "--archive")

    dependsOn("npmInstall")
}
```

## Build Integration

### Option 1: Manual Execution
```bash
./gradlew generateWorkIndex
./gradlew archiveDoneTasks
```

### Option 2: Hook to Build (Optional)
```kotlin
// Run after successful build
tasks.named("build") {
    finalizedBy("generateWorkIndex")
}
```

### Option 3: Git Hook (Recommended)
Create `.githooks/post-commit`:
```bash
#!/bin/bash
# Regenerate work index after commit
./gradlew generateWorkIndex --quiet
git add work/WORK_INDEX.md
git commit --amend --no-edit
```

## CI/CD Integration (Future)

### GitHub Actions Workflow
```yaml
name: Update Work Index

on:
  push:
    paths:
      - 'work/tasks/**'
      - 'work/features/**'

jobs:
  update-index:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: ./gradlew generateWorkIndex
      - uses: stefanzweifel/git-auto-commit-action@v5
        with:
          commit_message: "chore: update WORK_INDEX.md"
          file_pattern: work/WORK_INDEX.md
```

## Files to Modify
- `build.gradle.kts` - Add script tasks

## Files to Create (Future)
- `.github/workflows/work-index.yml`
- `.githooks/post-commit`

## Definition of Done
- `./gradlew generateWorkIndex` works
- `./gradlew archiveDoneTasks` works
- npm install runs automatically when needed
- Tasks have proper descriptions
- Works on Windows, macOS, Linux
