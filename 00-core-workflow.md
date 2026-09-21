---
name: Core workflow
description: Step discipline, planning, and stop conditions for every task in this repo.
activation: always_on
---

# Core workflow

Inherits everything in `@../../AGENTS.md`. This file adds Antigravity-specific
execution behaviour.

## Before writing code

Produce a short plan artifact containing:

- the step id from `BUILD_PLAN.md`
- the files you will create or modify (exact paths)
- the verification you will run
- anything you are unsure about

If the file list exceeds 8 files, the step is too big — split it and say so.

## While working

- Use the task list so each sub-action is visible. Mark items complete as you
  go, not all at the end.
- Prefer reading an existing file over regenerating it. Edit surgically.
- After each file write, re-read the file if a later edit depends on it.
- Run `./gradlew :module:compileDebugKotlin` on the touched module before
  running the full build. Fail fast and cheap.

## Browser and terminal use

- Use the browser tool only to read official docs (developer.android.com,
  supabase.com/docs, kotlinlang.org). Do not copy code from forums without
  saying where it came from.
- Terminal commands must be non-destructive. Never run `rm -rf`, `git reset
  --hard`, `git clean`, or force-push. If you believe a destructive action is
  needed, ask.
- Never run a command that needs an interactive login. Ask the owner to do it.

## Verification before reporting done

- The named check in `BUILD_PLAN.md` passed, and you show its output.
- `./gradlew ktlintCheck detekt testDebugUnitTest` is green for touched modules.
- `PROJECT_STATE.md` is updated in the same commit.

If a check fails twice for the same reason, stop and report. Do not try a third
variation, and never disable a lint rule, delete a failing test, or add
`@Suppress` to make a check pass.

## Never do without explicit approval

- Add a dependency, change `minSdk`/`targetSdk`, or alter the module graph.
- Modify `DESIGN_AND_SCOPE.md` decisions D1–D10.
- Touch `supabase/migrations/` files that are already applied — write a new
  migration instead.
- Commit anything under `.aiexclude` or `local.properties`.
- Start the next step because the current one finished early.
