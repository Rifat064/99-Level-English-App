# AGENTS.md

Cross-tool agent rules for this repository. Read by Antigravity and by Gemini
Agent Mode in Android Studio. These rules are always in force.

## Project

Android vocabulary app for Bangladeshi exam candidates (BCS, Bank, IELTS, BBA,
govt jobs). One daily card = 2 words + 1 sentence + 1 AI image + Bangla
meanings. Kotlin, Jetpack Compose, Supabase, offline-first.

## Source of truth

| File | Role |
|---|---|
| `DESIGN_AND_SCOPE.md` | what to build, stack, data model, locked decisions D1–D10 |
| `BUILD_PLAN.md` | the ordered steps; never work out of order |
| `PROJECT_STATE.md` | what is done, what is blocked, open questions |

Read all three at the start of every session. If a request conflicts with them,
stop and say so. Do not silently resolve the conflict.

## The loop

1. Open `PROJECT_STATE.md`, find the current step.
2. Restate the step's goal and acceptance check in one line before coding.
3. Implement **that step only**.
4. Run the verification named in `BUILD_PLAN.md`.
5. Update `PROJECT_STATE.md`: snapshot, checkbox, session-log row.
6. Commit as `[P{phase}.{step}] description`.
7. Stop. Report what changed and what the next step is.

One step per turn. Do not chain steps. Do not "while I was in there" other
files.

## Hard rules

- **Never invent a dependency.** Only libraries listed in `DESIGN_AND_SCOPE.md`
  §3. Anything else needs approval. Pin versions in
  `gradle/libs.versions.toml` — no version literals in module Gradle files.
- **Never commit a secret.** Keys live in `local.properties` (gitignored) and
  reach code via `BuildConfig`. The Supabase **service-role key must never
  appear in the Android app** under any circumstance.
- **Never weaken security to unblock a feature.** If RLS denies the client, fix
  the policy or move the work into an edge function.
- **Never write entitlement from the client.** `subscriptions` is written only
  by the server after purchase verification.
- **Never bundle premium content in the APK.** Paid images come from
  short-lived signed URLs.
- **Never trust the device clock** for content unlocking. Day index is computed
  server-side from `profiles.enrolled_at`.
- **Never bake text into a generated image.** Speech bubbles and captions are
  drawn in Compose.
- **Never auto-translate Bangla into production.** The human review gate in the
  content pipeline is blocking.
- **Never delete or rewrite history** in `PROJECT_STATE.md`. Append only.
- **Never mark a step done without running its check.** "Should work" is not a
  verification.

## When to stop and ask

- The step needs a credential, a price, or an account the repo does not have.
- The spec is ambiguous and a guess would be expensive to reverse.
- The fix requires changing a locked decision (D1–D10).
- A test fails for a reason you do not understand.

State the question, give the options you see, and wait. Do not proceed on an
assumption and flag it later.

## Code conventions

- Kotlin official style, 4-space indent, 100-col soft wrap. ktlint + detekt must
  pass.
- Packages: `feature-*` modules never import each other. Shared code goes to
  `core-*`. `core-model` has zero Android imports.
- ViewModels expose a single immutable `UiState` via `StateFlow`. No
  `LiveData`. No mutable state exposed publicly.
- Composables are stateless; state is hoisted. Every screen has a `@Preview`.
- No `!!`. No `GlobalScope`. No blocking calls on the main dispatcher.
- No hardcoded user-visible strings — `strings.xml` and `values-bn/strings.xml`.
- Errors surface as typed `Result`/sealed classes, never swallowed catches.
- Suspend functions take a `CoroutineDispatcher` parameter for testability.

## Output discipline

- Diffs only for what the step requires. No drive-by refactors, no reformatting
  untouched files, no renaming things you were not asked to rename.
- No TODO comments left behind — either implement it or record it in
  `PROJECT_STATE.md` Known issues.
- No mock or sample data in `main` source sets. Fakes live in `debug` or tests.
- Explanations in chat stay under ten lines. The code and the state file carry
  the detail.
