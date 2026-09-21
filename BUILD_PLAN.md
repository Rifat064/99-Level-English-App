# BUILD_PLAN.md

Execution plan for the agentic IDE. Work through phases **in order**. Do not
start a phase until the previous phase's exit criteria are all checked in
`PROJECT_STATE.md`.

---

## Operating rules for the agent

1. **One step per turn.** Complete a single numbered step, run its verification,
   update `PROJECT_STATE.md`, then stop and report. Do not chain steps.
2. **Read before writing.** Open `DESIGN_AND_SCOPE.md` and `PROJECT_STATE.md` at
   the start of every session.
3. **No invented dependencies.** If a library is not listed in
   `DESIGN_AND_SCOPE.md` §3, ask before adding it. Pin every version in
   `gradle/libs.versions.toml`.
4. **No placeholder data that can reach production.** Fake content must live in
   a `debug` source set or a `@Preview`, never in `main`.
5. **No secrets in tracked files.** Read keys from `local.properties` →
   `BuildConfig`. `local.properties` stays in `.gitignore`.
6. **Do not weaken security to make a feature work.** If RLS blocks the client,
   fix the policy or move the logic to an edge function. Never switch to the
   service-role key in the app.
7. **Ask, do not assume**, when: a product rule is ambiguous, a step requires an
   owner-only credential, or a fix requires changing a locked decision (D1–D10).
8. **Verification is part of the step.** A step is not done until its stated
   check passes and the result is recorded.
9. **Commit granularity:** one commit per step, message
   `[P{phase}.{step}] short description`.

---

## Phase 0 — Foundations

**Goal:** an empty app that builds, signs, and runs on a device.

| Step | Work | Verify |
|---|---|---|
| 0.1 | Create Gradle project, `com.vocabdaily.app` (rename if the owner supplies a package). Kotlin 2.0, AGP current, minSdk 24, targetSdk 35. Version catalog set up. | `./gradlew assembleDebug` succeeds |
| 0.2 | Create the module skeleton from §3 of the design doc. Every module has a `build.gradle.kts` and an empty package. | Gradle sync clean; module graph has no cycles |
| 0.3 | Add Hilt, Compose BOM, Material 3, Coil, Room, DataStore, Navigation Compose. Wire `@HiltAndroidApp`. | app launches to a blank screen |
| 0.4 | Design tokens in `core-ui`: color scheme (light + dark), typography with bundled Noto Sans Bengali, spacing scale, shape scale. | a preview screen renders Bangla conjuncts `ক্ষ ত্র জ্ঞ ঙ্গ` correctly |
| 0.5 | Detekt + ktlint + a CI workflow that runs `lint`, `detekt`, `testDebugUnitTest`. | CI green on a trivial PR |

**Exit:** blank themed app installs and runs; CI passes.

---

## Phase 1 — Backend and content

**Goal:** real content exists in Supabase before any feature reads it.

| Step | Work | Verify |
|---|---|---|
| 1.1 | Create the Supabase project. Write `supabase/migrations/0001_init.sql` implementing every table in §4. | migration applies cleanly on a fresh DB |
| 1.2 | Write `0002_rls.sql` with the policies from §4. Include a policy test script that asserts user A cannot read user B's `user_progress`. | policy test passes |
| 1.3 | Build `/pipeline` scripts 01–04 and 07. Use `words_master.csv` as input. Emit `05_review.csv`. | 90 draft rows generated for season 1 |
| 1.4 | **Owner task, not the agent's:** review and correct `05_review.csv` Bangla by hand. Agent waits. | owner marks the gate complete in `PROJECT_STATE.md` |
| 1.5 | Build `06_image.py` and `08_upload.py`. Generate 90 images, convert to WebP + blurhash, upload to Storage, insert `cards` rows with `status='reviewed'`. | 90 cards visible in Supabase with working image paths |
| 1.6 | Edge function `get-signed-image`: takes `card_id`, checks entitlement (free window or active subscription), returns a 10-minute signed URL or 403. | curl with a non-subscriber JWT on an old card returns 403 |
| 1.7 | Edge function `get-daily-card`: computes the caller's `day_index` from `profiles.enrolled_at` server-side and returns today's card payload. | changing the device clock does not change the returned card |

**Exit:** 90 published cards, RLS proven, both edge functions callable.

---

## Phase 2 — Auth and shell

| Step | Work | Verify |
|---|---|---|
| 2.1 | `core-network`: Supabase client singleton, DTOs, DTO↔domain mappers. | unit tests on mappers pass |
| 2.2 | `feature-auth`: Google sign-in via **Credential Manager** into Supabase Auth. Handle cancel, no-account, and no-network cases explicitly. | sign-in works on a device with and without a Google account |
| 2.3 | Session persistence and silent refresh; splash routes to home or login without a visible flash. | kill and relaunch keeps the session |
| 2.4 | On first successful login, create the `profiles` row with `enrolled_at = now()` and `timezone` from the device. | row appears; repeat logins do not reset it |
| 2.5 | Navigation graph for all 10 screens with stub destinations. Deep link `vocabdaily://card/today`. | every route reachable; back stack behaves |
| 2.6 | Sign out and **delete account** (Play requirement): edge function cascades the user's rows. | account deleted; data gone from all tables |

**Exit:** a user can sign in, is persisted, can sign out and delete the account.

---

## Phase 3 — Home and the daily card (MVP core)

| Step | Work | Verify |
|---|---|---|
| 3.1 | Onboarding: 3 panes on the value of vocabulary for BCS / Bank / IELTS / BBA / govt jobs / speaking, plus notification-time picker. Shown once, flagged in DataStore. | reinstall shows it, second launch does not |
| 3.2 | Home screen: value pitch sections, streak widget, today's status chip, and the **"Start your journey"** button per §8.4. | button opens day 1 for a new user, today's card for a returning one |
| 3.3 | Room schema + DAOs for cards, words, progress. Migration test from v1 to v1 (baseline). | Room schema JSON exported and committed |
| 3.4 | `CardRepository`: offline-first. Room is the single source of truth; the network refreshes it. Expose `Flow<Result<Card>>`. | airplane mode still shows the cached card |
| 3.5 | Daily card UI: image with blurhash placeholder, Compose speech-bubble overlay (no text in the image), sentence, two word blocks with Bangla and exam tag, "What's happening" translation. | matches the card anatomy in §1 at 320dp and 600dp widths |
| 3.6 | Interactions: "Mark as learned" (writes `user_progress`), bookmark toggle, TTS playback of `sentence_en`. | progress survives reinstall via server sync |
| 3.7 | Streak logic: increment on completion, reset after a missed day, with a one-day grace. Compute against server day index, not local time. | simulated date jumps behave correctly |
| 3.8 | WorkManager daily notification at `profiles.notify_hour`, rescheduled on boot and on setting change. Deep links to today's card. | fires after reboot; no duplicates |

**Exit:** a real user can install, sign in, and use the app daily with no paid
features. This is the internal-testing milestone.

---

## Phase 4 — History, paywall, payment

| Step | Work | Verify |
|---|---|---|
| 4.1 | History screen: weekly grid, free items open, locked items show blurred thumbnail + lock badge per §5. | locked cards never fetch a full-resolution image |
| 4.2 | `core-billing`: define `BillingProvider` (`queryProducts`, `purchase`, `restore`, `observeEntitlement`) with a fake implementation for debug builds. | UI can be driven entirely by the fake |
| 4.3 | `PlayBillingProvider` using Play Billing 7+. Products: monthly, 6-month, lifetime. | purchase flow completes in a Play internal-test track |
| 4.4 | Edge function `verify-purchase`: validates the token against the Google Play Developer API server-side and writes `subscriptions`. Client never writes that table. | a forged client-side entitlement is rejected |
| 4.5 | Real-time entitlement gating across history, quiz history, bookmark limit, and words-per-day. | downgrading an account re-locks content within one app resume |
| 4.6 | Play Billing RTDN webhook for renewals, cancellations, refunds, grace period. | a refund in the console flips status to `refunded` |
| 4.7 | Paywall screen with restore-purchases and a clear price in BDT. | restore works on a fresh install of the same account |

**Exit:** money can be taken and entitlement is server-truth.

---

## Phase 5 — Weekly quiz

| Step | Work | Verify |
|---|---|---|
| 5.1 | `QuizGenerator` in pure Kotlin implementing the 5 question types and all rules in §6. No Android dependencies. | property tests: every word appears ≥1; no distractor equals the answer; seed reproducibility |
| 5.2 | Quiz availability: unlocked Monday for the prior week; free users get the most recent week only. | Sunday shows a countdown, Monday unlocks |
| 5.3 | Quiz UI: one question per screen, progress bar, no back-editing after submit. | state survives process death |
| 5.4 | Result screen with per-word breakdown and "review missed words"; write `quiz_attempts`. | unique constraint prevents duplicate attempts per week |

**Exit:** a quiz can be taken end to end offline.

---

## Phase 6 — Retention layer

| Step | Work | Verify |
|---|---|---|
| 6.1 | Spaced-repetition deck (SM-2 lite) fed by bookmarks, self-ratings, and missed quiz words. | scheduling unit tests |
| 6.2 | Share-as-image: render the card Composable to a bitmap, watermark for free users, share sheet. | output is legible at 1080x1350 |
| 6.3 | Exam-tag filter and search over the word pool. | filtering by `BCS` returns only BCS-tagged words |
| 6.4 | Settings: notification time, words per day (subscriber only), TTS accent, theme. | changes apply without a restart |
| 6.5 | Catch-up flow for missed days, respecting the free window. | a user 5 days behind is offered the right entry point |

---

## Phase 7 — Release

| Step | Work | Verify |
|---|---|---|
| 7.1 | Crash reporting (Sentry or Firebase Crashlytics) and minimal, privacy-safe analytics. | a forced crash is reported |
| 7.2 | ProGuard/R8 rules; verify release build does not break Supabase serialization. | release APK runs the full happy path |
| 7.3 | Play Data Safety form, privacy policy, account-deletion URL. | Play pre-launch report clean |
| 7.4 | Accessibility pass: TalkBack labels, contrast, 200% font scale. | no clipped text on the card screen |
| 7.5 | Localization pass: every string in `values/` and `values-bn/`. | no hardcoded strings flagged by lint |
| 7.6 | Season 2 content pipeline run and publish (days 91–180). | app rolls past day 90 with no gap |

---

## Risk register the agent must respect

| Risk | Mitigation baked into the plan |
|---|---|
| Premium content extractable from the APK | images live behind signed URLs (1.6), never bundled |
| Clock manipulation unlocks content | server-side day index (1.7) |
| Play rejects a third-party gateway | Play Billing is primary (D3), local gateways only for direct-APK builds |
| Bad Bangla in production | human review gate (1.4) is blocking, not advisory |
| Bangla renders as boxes or wrong conjuncts | bundled font verified in 0.4 |
| Content runs out | seasons (D9) plus the Phase 7.6 rollover |
| Free tier exceeded | egress trigger documented in §10 |
