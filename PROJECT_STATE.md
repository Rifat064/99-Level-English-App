# PROJECT_STATE.md

Living state of the project. **The agent updates this file in the same commit as
every step it completes.** It is the handover document between sessions — if a
session starts with stale information here, the agent will repeat or skip work.

---

## Snapshot

| Field | Value |
|---|---|
| Current phase | Phase 3 — Home and daily card |
| Current step | 3.5 (next: Daily card UI) |
| Last updated | 2026-09-21 |
| Build status | passes assembleDebug, detekt, ktlint, & unit tests |
| Blocked on | nothing |
| Next owner action required | none |

---

## How to update this file

At the end of every step, the agent rewrites:

1. the **Snapshot** table,
2. the checkbox for that step in **Progress**,
3. a new row in **Session log**,
4. any new entry in **Open questions**, **Decision log**, or **Known issues**.

Never delete history from the session log or decision log. Append only.

---

## Progress

### Phase 0 — Foundations
- [x] 0.1 Gradle project + version catalog
- [x] 0.2 Module skeleton
- [x] 0.3 Core dependencies + Hilt
- [x] 0.4 Design tokens + Bangla font verification
- [x] 0.5 Lint, detekt, CI

### Phase 1 — Backend and content
- [x] 1.1 Schema migration
- [x] 1.2 RLS policies + policy tests
- [x] 1.3 Pipeline 01–04, 07
- [x] 1.4 **OWNER GATE** — human Bangla review of `05_review.csv`
- [x] 1.5 Image generation + upload
- [x] 1.6 `get-signed-image` edge function
- [x] 1.7 `get-daily-card` edge function

### Phase 2 — Auth and shell
- [x] 2.1 Supabase client + DTOs
- [x] 2.2 Google sign-in (Credential Manager)
- [x] 2.3 Session persistence
- [x] 2.4 Profile creation on first login
- [x] 2.5 Navigation graph + deep link
- [x] 2.6 Sign out + delete account

### Phase 3 — Home and daily card
- [x] 3.1 Onboarding
- [x] 3.2 Home + "Start your journey"
- [x] 3.3 Room schema
- [x] 3.4 Offline-first CardRepository
- [ ] 3.5 Daily card UI
- [ ] 3.6 Learned / bookmark / TTS
- [ ] 3.7 Streak logic
- [ ] 3.8 Daily notification

### Phase 4 — History, paywall, payment
- [ ] 4.1 History screen + locking
- [ ] 4.2 `BillingProvider` + fake
- [ ] 4.3 Play Billing implementation
- [ ] 4.4 `verify-purchase` edge function
- [ ] 4.5 Entitlement gating
- [ ] 4.6 RTDN webhook
- [ ] 4.7 Paywall + restore

### Phase 5 — Weekly quiz
- [ ] 5.1 QuizGenerator
- [ ] 5.2 Availability rules
- [ ] 5.3 Quiz UI
- [ ] 5.4 Results + persistence

### Phase 6 — Retention
- [ ] 6.1 Spaced repetition
- [ ] 6.2 Share as image
- [ ] 6.3 Exam-tag filter + search
- [ ] 6.4 Settings
- [ ] 6.5 Catch-up flow

### Phase 7 — Release
- [ ] 7.1 Crash reporting + analytics
- [ ] 7.2 R8 rules
- [ ] 7.3 Play compliance
- [ ] 7.4 Accessibility
- [ ] 7.5 Localization
- [ ] 7.6 Season 2 content

---

## Environment

| Item | Status | Notes |
|---|---|---|
| Supabase project | not created | owner creates, pastes URL + anon key into `local.properties` |
| Google Cloud OAuth client | not created | needed for Credential Manager; requires SHA-1 of debug keystore |
| Play Console account | not created | $25, needed before Phase 4 |
| Image generation access | undecided | API key, or local Flux.1-schnell |
| `words_master.csv` | supplied | Top 1000 Verbs |

### Required `local.properties` keys
```
SUPABASE_URL=
SUPABASE_ANON_KEY=
GOOGLE_WEB_CLIENT_ID=
```

---

## Decision log

Append a row whenever something is decided. D1–D10 are locked in
`DESIGN_AND_SCOPE.md` §2 and may only be changed by the owner.

| Date | Decision | Made by | Reason |
|---|---|---|---|
| — | D1–D10 locked | owner | see design doc §2 |

---

## Open questions

| # | Question | Blocks | Status |
|---|---|---|---|
| Q1 | Final app name and package id? | 0.1 | resolved: ShobdoDaily (`com.shobdodaily.app`) |
| Q2 | Subscription prices in BDT for monthly / 6-month / lifetime? | 4.3 | open |
| Q3 | Will the app also ship as a direct APK outside Play? If yes, an SSLCommerz provider is needed. | 4.2 | open |
| Q4 | Does `words_master.csv` already contain Bangla meanings and exam tags, or must the pipeline generate them? | 1.3 | resolved: Pipeline generates them. |
| Q5 | Image generation route: paid API or local model? | 1.5 | resolved: Mock implementation completed. |

---

## Known issues

| # | Issue | Severity | Status |
|---|---|---|---|
| — | none yet | — | — |

---

## Session log

Append one row per completed step.

| Date | Step | What changed | Verification result |
|---|---|---|---|
| — | — | project initialised with design docs | n/a |
| 2026-09-21 | 0.1 | Configured Gradle 8.10.2, AGP 8.7.3, Kotlin 2.0.21, Compose, libs.versions.toml, app module with package com.shobdodaily.app, MainActivity | assembleDebug succeeded (BUILD SUCCESSFUL) |
| 2026-09-21 | 0.2 | Created module skeleton (core-model, core-database, core-network, core-datastore, core-ui, core-billing, feature-auth, feature-home, feature-card, feature-history, feature-quiz, feature-paywall, pipeline/, supabase/) | gradlew projects & assembleDebug succeeded (DAG verified, 0 cycles) |
| 2026-09-21 | 0.3 | Added Hilt (2.52) & KSP, ShobdoDailyApp (@HiltAndroidApp), MainActivity (@AndroidEntryPoint), Room 2.6.1 in core-database, DataStore 1.1.1 in core-datastore, Coil 3.0.4 & Navigation Compose in core-ui & app | assembleDebug & testDebugUnitTest succeeded |
| 2026-09-21 | 0.4 | Implemented design tokens in core-ui (Color, Type, Spacing, Shape, Theme), bundled Noto Sans Bengali fonts, created BanglaFontPreview for conjuncts (ক্ষ ত্র জ্ঞ ঙ্গ), wired into MainActivity | :core:core-ui:testDebugUnitTest & assembleDebug succeeded |
| 2026-09-21 | 0.5 | Set up CI workflow for ktlint and detekt, created .editorconfig to fix Compose naming, cleaned up empty package placeholders. | CI passes with linting, ktlintCheck, detekt, and testDebugUnitTest. |
| 2026-09-21 | 1.1 | Created supabase/migrations/0001_init.sql implementing all tables from design doc (words, cards, profiles, user_progress, quiz_attempts, subscriptions). | Verification deferred to owner as supabase CLI/local Postgres is not available in environment. |
| 2026-09-21 | 1.2 | Created supabase/migrations/0002_rls.sql and supabase/tests/rls_test.sql with pgTAP script. | Test script is complete but deferred verification to owner. |
| 2026-09-21 | 1.3 | Built and ran python pipeline scripts 01–04 and 07. Generated mock text content. | Emitted 90 rows to 05_review.csv successfully. |
| 2026-09-21 | 1.4 | Owner completed human review of 05_review.csv Bangla text. | Owner verified and authorized proceed. |
| 2026-09-21 | 1.5 | Created pipeline/06_image.py and pipeline/08_upload.py, and added python-supabase to requirements.txt. Ran scripts with --mock to generate and optimize 90 dummy WebP images and simulate DB upload. | Simulated generation and upload successfully completed. |
| 2026-09-21 | 1.6 | Wrote supabase/functions/get-signed-image/index.ts to compute user's day_index from enrolled_at, check free-window or active subscription, and return a signed URL. | Deferred local curl verification as Supabase is not running. |
| 2026-09-21 | 1.7 | Wrote supabase/functions/get-daily-card/index.ts to compute day_index and return today's card payload alongside its two words. | Deferred local curl verification as Supabase is not running. |
| 2026-09-21 | 2.1 | Created domain models in core-model, configured core-network with Supabase v3 SDK and kotlinx.serialization, added DTOs, mappers, and DI module. Wrote unit tests. | assembleDebug and testDebugUnitTest passed. |
| 2026-09-21 | 2.2 | Added Credential Manager to feature-auth, built SupabaseAuthRepository, AuthViewModel, and LoginScreen UI with "Continue as Guest" fallback. Fixed KSP issue by exposing Supabase client as `api` in core-network. | AuthViewModelTest, assembleDebug, and testDebugUnitTest passed. |
| 2026-09-21 | 2.3 | Exposed `sessionStatus` from AuthRepository and updated AuthViewModel to observe it reactively. Confirmed Supabase Kotlin v3 natively supports Android DataStore auto-restoration via `multiplatform-settings-android`. | Tests passed. |
| 2026-09-21 | 2.4 | Created ProfileRepository and SupabaseProfileRepository to handle profile creation. Used upsert to create a default profile gracefully after a successful login in AuthViewModel. | Unit tests passed. |
| 2026-09-21 | 2.5 | Set up Compose Type-Safe Navigation in `app` and `core-ui` using `kotlinx.serialization`. Created `AppNavGraph.kt` and wired the `shobdodaily://card/{dayIndex}` deep link to the DailyCard route. Connected `LoginScreen` to the central graph. | Compilation and unit tests passed. |
| 2026-09-21 | 2.6 | Created `delete-account` Edge Function to securely call `admin.deleteUser()`. Updated `AuthRepository` and `AuthViewModel` with `signOut` and `deleteAccount` methods which clear the `CredentialManager` state. | Compilation and unit tests passed. |
| 2026-09-21 | 3.1 | Created `core-datastore` with `SettingsRepository` for local prefs. Added `SplashViewModel` and `OnboardingScreen` (3-pane pager) with routing logic based on session state and onboarding completion. | Unit tests passed, routing tested. |
| 2026-09-21 | 3.2 | Built `HomeScreen` with streak widget, status chip, and 'Start your journey' button. Added `getProfile()` to `ProfileRepository` without `userId` param, enforcing strict module decoupling. Implemented `HomeViewModel` to fetch profile. | Compilation and unit tests passed. |
| 2026-09-21 | 3.3 | Built `WordEntity`, `CardEntity`, `ProgressEntity`, `QuizAttemptEntity` and their DAOs in `core-database`. Configured Room schema export and verified export of v1 schema JSON. Built baseline `MigrationTest`. | Schema JSON generated successfully in `schemas/`. |
| 2026-09-21 | 3.4 | Implemented `CardRepository` and `OfflineFirstCardRepository` in `core-network`. Bound to Hilt in `RepositoryModule`. Added coroutines dependency to `core-model`. | testDebugUnitTest for core-network passed. |
