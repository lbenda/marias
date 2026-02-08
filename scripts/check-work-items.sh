#!/usr/bin/env bash
set -euo pipefail

ERRORS_FOUND=0

error() {
  echo "❌ $1"
  ERRORS_FOUND=1
}

info() {
  echo "ℹ️  $1"
}

check_id_filename() {
  local pattern="$1"
  local dir="$2"

  for f in "$dir"/*.md; do
    [ -e "$f" ] || continue
    local name
    name=$(basename "$f")
    if [[ ! "$name" =~ $pattern ]]; then
      error "Invalid filename in $dir: $name"
    fi
  done
}

check_section() {
  local file="$1"
  local section="$2"
  if ! grep -q "^## $section" "$file"; then
    error "Missing section '## $section' in $file"
  fi
}

check_metadata() {
  local file="$1"
  local key="$2"
  if ! grep -q "^- $key:" "$file"; then
    error "Missing metadata '- $key:' in $file"
  fi
}

info "Checking rules (R-###)..."
check_id_filename '^R-[0-9]{3}-.*\.md$' docs/rules

info "Checking features (F-###)..."
check_id_filename '^F-[0-9]{3}-.*\.md$' work/features
for f in work/features/F-*.md; do
  check_section "$f" "Description"
  check_metadata "$f" "Type"
  check_metadata "$f" "Status"
  check_metadata "$f" "Source"
done

info "Checking tasks (T-###)..."
check_id_filename '^T-[0-9]{3}-.*\.md$' work/tasks
for f in work/tasks/T-*.md; do
  check_section "$f" "Definition of Done"
  check_section "$f" "Goal"
  check_section "$f" "Scope"
done

info "Checking bugs (B-###)..."
check_id_filename '^B-[0-9]{3}-.*\.md$' work/bugs
for f in work/bugs/B-*.md; do
  check_section "$f" "Steps to reproduce"
  check_section "$f" "Actual behavior"
done

info "Checking ADRs (A-###)..."
check_id_filename '^A-[0-9]{3}-.*\.md$' docs/adr
for f in docs/adr/A-*.md; do
  check_section "$f" "Decision"
done

if [ $ERRORS_FOUND -eq 0 ]; then
  echo "✅ All work items look good."
  exit 0
else
  echo "❌ Some checks failed."
  exit 1
fi
