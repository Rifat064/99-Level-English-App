# DESIGN_AND_SCOPE.md

Authoritative product and architecture specification. Any agent working in this
repository must read this file before writing code. If a request conflicts with
this document, stop and ask rather than improvising.

---

## 1. Product

A daily English-vocabulary Android app for Bangladeshi exam candidates (BCS,
Bank, IELTS, BBA, government jobs) and general spoken-English learners.

Each day the user receives one **card**:

- one memorable English sentence containing exactly two target words
- one illustration of that sentence (AI-generated, no text baked in)
- Bangla meaning of each word, with exam tag (e.g. `BCS 35`, `Janata Bank 2019`)
- Bangla translation of the full sentence

The illustration exists for memory, not decoration. Sentence and image must be
concrete and scene-like so the picture can actually be drawn.

### Card anatomy (rendered in app, not burned into the image)

```
[ IMAGE ]                      <- AI scene only
[ speech bubble overlay ]      <- drawn in Compose over the image
➡️  sentence_en
🔍  Vocabulary to focus
🏖️  word_a = (bangla_a)   [exam_tag_a]
💔  word_b = (bangla_b)   [exam_tag_b]
🪄  What's happening
     sentence_bn
```

### Non-goals

- No social feed, no comments, no user-generated content.
- No on-device LLM. Quizzes are rule-generated (see §6).
- No iOS in v1.
- No web app in v1.

---

## 2. Locked decisions

| # | Decision | Rationale |
|---|---|---|
| D1 | Android only, Kotlin + Jetpack Compose | single target, lowest cost |
| D2 | Supabase (Postgres + Auth + Storage + Edge Functions) | free tier, open source, self-hostable later |
| D3 | Google Play Billing is the primary payment path | Play policy forbids third-party gateways for digital goods |
| D4 | Payment code sits behind a `BillingProvider` interface | lets an SSLCommerz/bKash provider be added for direct-APK distribution without touching feature code |
| D5 | Quiz generation is rule-based Kotlin, on device | free, offline, deterministic |
| D6 | Notifications use WorkManager local notifications | schedule is deterministic; no push server needed in v1 |
| D7 | Images carry no rendered text | image models misspell; text is drawn by the app |
| D8 | Content is authored offline by a Python pipeline and imported | app never calls an image or LLM API at runtime |
| D9 | Content ships in **seasons of 90 days** | avoids pre-generating thousands of unused images |
| D10 | Day index is derived from a server-stored `enrolled_at` | prevents unlocking content by changing the device clock |

---

## 3. Tech stack

| Layer | Choice | Notes |
|---|---|---|
| Language | Kotlin 2.0+ | |
| UI | Jetpack Compose, Material 3 | |
| Architecture | MVVM + unidirectional data flow | ViewModel exposes immutable `UiState` |
| DI | Hilt | |
| Local DB | Room | offline-first cache |
| Key-value | DataStore (Preferences) | settings, onboarding flags |
| Network | Supabase Kotlin SDK (Ktor under the hood) | |
| Auth | Supabase Auth + Android Credential Manager | the legacy Google Sign-In SDK is deprecated — do not use it |
| Images | Coil 3, WebP, BlurHash placeholder | |
| Storage/CDN | Supabase Storage (v1) → Cloudflare R2 if egress grows | |
| Background work | WorkManager | |
| Billing | Google Play Billing Library 7+ | behind `BillingProvider` |
| TTS | Android `TextToSpeech` | free, no API |
| Testing | JUnit5, Turbine, MockK, Compose UI tests | |
| Content pipeline | Python 3.11 scripts in `/pipeline` | not shipped in the APK |

### Module layout

```
app/                 navigation, DI wiring, theme, MainActivity
core/
  core-model/        pure Kotlin data classes, no Android deps
  core-database/     Room entities, DAOs, migrations
  core-network/      Supabase client, DTOs, mappers
  core-datastore/    preferences
  core-ui/           shared Composables, design tokens
  core-billing/      BillingProvider interface + Play implementation
feature/
  feature-auth/      login, Google sign-in
  feature-home/      landing, value pitch, Start your journey
  feature-card/      daily card screen, share, TTS
  feature-history/   archive, paywall gating
  feature-quiz/      weekly quiz engine + UI
  feature-paywall/   subscription screens
pipeline/            python content tooling (not built into app)
supabase/            SQL migrations, RLS policies, edge functions
```

---

## 4. Data model

### Supabase (Postgres)

```sql
words (
  id bigserial pk,
  word text unique not null,
  pos text,                        -- noun / verb / adj
  bangla text not null,
  english_gloss text,
  exam_tag text,                   -- 'BCS 35', 'Janata Bank 2019'
  exam_category text,              -- BCS | BANK | IELTS | GOVT | GENERAL
  difficulty smallint,             -- 1..5
  frequency_rank int,
  created_at timestamptz default now()
)

cards (
  id bigserial pk,
  day_index int unique not null,   -- 1..N, the global curriculum position
  season int not null,
  word_a_id bigint fk -> words,
  word_b_id bigint fk -> words,
  sentence_en text not null,
  sentence_bn text not null,
  bubble_text text,                -- optional short line for the overlay
  image_path text not null,        -- storage key, not a public URL
  image_blurhash text,
  status text not null,            -- draft | reviewed | published
  published_at timestamptz
)

profiles (
  id uuid pk references auth.users,
  display_name text,
  enrolled_at timestamptz not null default now(),
  words_per_day smallint default 2,   -- 2 | 4 | 6
  timezone text default 'Asia/Dhaka',
  notify_hour smallint default 8,
  streak_count int default 0,
  longest_streak int default 0,
  last_completed_day int default 0
)

user_progress (
  user_id uuid fk,
  card_id bigint fk,
  seen_at timestamptz,
  completed boolean default false,
  bookmarked boolean default false,
  self_rating smallint,            -- 1 easy .. 3 hard, feeds review deck
  primary key (user_id, card_id)
)

quiz_attempts (
  id bigserial pk,
  user_id uuid fk,
  week_index int not null,
  score smallint,
  total smallint,
  answers jsonb,
  taken_at timestamptz default now(),
  unique (user_id, week_index)
)

subscriptions (
  user_id uuid pk fk,
  provider text not null,          -- play | sslcommerz | bkash
  product_id text,
  status text not null,            -- active | grace | expired | refunded
  purchase_token text,
  expires_at timestamptz,
  updated_at timestamptz
)
```

### Row Level Security (mandatory)

- `profiles`, `user_progress`, `quiz_attempts`, `subscriptions`: user may read
  and write only rows where `user_id = auth.uid()`.
- `subscriptions.status` is writable **only** by the service role from the
  purchase-verification edge function. The client never writes it.
- `words`: public read.
- `cards`: public read of columns **excluding** `image_path` for unpublished or
  locked rows. Enforce entitlement in the edge function that issues signed image
  URLs — never rely on the client hiding a URL.

### Room (local mirror)

`CardEntity`, `WordEntity`, `ProgressEntity`, `QuizAttemptEntity`,
`SettingsPrefs`. Only the **free/current** card image is cached to disk. Premium
archive images are fetched on demand with short-lived signed URLs.

---

## 5. Access model

| Content | Free | Subscriber |
|---|---|---|
| Today's card | yes | yes |
| Last 7 days | yes | yes |
| Full archive (all past cards) | locked preview (word list only, no image, no sentence) | yes |
| Weekly quiz | last week only | all weeks |
| Bookmarks / review deck | max 20 items | unlimited |
| Words per day | 2 | 2 / 4 / 6 selectable |
| Share card image | yes (with watermark) | yes |

Free users always see enough value to build the habit. The paywall sells
**catch-up and review**, not the daily habit itself.

---

## 6. Weekly quiz engine (rule-based, no AI)

Runs on device. Input: the 7 cards from the previous week (14 words). Output: a
12-question quiz.

Question types, generated from existing fields:

1. **English → Bangla MCQ.** Correct answer is `words.bangla`. Distractors are 3
   other words with the same `pos` and a `difficulty` within ±1, drawn from the
   full word pool.
2. **Bangla → English MCQ.** Inverse of the above.
3. **Fill in the blank.** Take `sentence_en`, replace the target word with `____`,
   offer 4 options.
4. **Sentence recall.** Show the image, ask which word pair belongs to it.
5. **Odd one out.** Three words from last week plus one from a different
   `exam_category`.

Rules the generator must obey:
- No distractor may be a synonym of the answer (maintain a `synonym_blocklist`
  column or table if collisions appear in testing).
- Each of the 14 words appears at least once.
- Question order is shuffled with a seed derived from `user_id + week_index`, so
  a retake within the same week yields the same quiz.
- Scores under 60% push the missed words into the spaced-repetition deck.

---

## 7. Content pipeline (`/pipeline`, run manually by the owner)

```
words_master.csv          10,000 rows, owner-supplied
  ↓ 01_curate.py          rank, tag, filter to the season's 180 words
  ↓ 02_pair.py            pair words into 90 semantically compatible duos
  ↓ 03_sentence.py        LLM: write sentence_en + image prompt
  ↓ 04_translate.py       LLM: sentence_bn + verify word banglas
  ↓ 05_review.csv         >>> HUMAN REVIEW GATE — owner edits Bangla by hand <<<
  ↓ 06_image.py           generate 1024x1024, no text in prompt
  ↓ 07_optimize.py        WebP ~100KB, compute blurhash
  ↓ 08_upload.py          push to Supabase Storage + insert cards as 'reviewed'
  ↓ manual publish        flip status to 'published', set day_index
```

Image prompt template (keep text out of it):

```
Flat vector illustration, clean lines, soft colors, {SCENE}.
No text, no letters, no words, no signage, no captions.
Simple background, clear subject, expressive faces.
```

Hard rule: step 05 is never skipped or automated away. Machine Bangla that
reaches users costs more in reviews than it saves in time.

---

## 8. Screens

1. **Splash** — resolve session, route.
2. **Onboarding (3 panes)** — why vocabulary matters for BCS / Bank / IELTS /
   BBA / govt jobs / speaking; how the daily habit works; set notification time.
3. **Login** — Google sign-in via Credential Manager, plus email magic link as
   fallback. Anonymous browse allowed for the home page only.
4. **Home** — value pitch cards, streak widget, today's status, and the primary
   button **"Start your journey"** which opens day 1 for a new user or today's
   card for a returning one.
5. **Daily card** — image with Compose speech bubble, sentence, two word blocks
   with Bangla and exam tag, "What's happening" translation, TTS button,
   bookmark, share, "Mark as learned".
6. **History / Archive** — grid by week, locked items show a blurred thumbnail
   and a lock badge.
7. **Quiz** — available Monday for the prior week; one question per screen,
   result page with per-word breakdown.
8. **Bookmarks / Review** — spaced-repetition queue (P2).
9. **Paywall** — plan comparison, Play Billing purchase flow, restore purchases.
10. **Settings** — notification time, words per day, TTS accent, sign out,
    delete account (Play requirement), privacy policy link.

---

## 9. Design language

- Material 3, dynamic color off; fixed brand palette.
- Bangla text uses a bundled font with full conjunct support (Noto Sans Bengali).
  Do not rely on the system font — many devices render juktakkhor incorrectly.
- Minimum touch target 48dp. Test at 320dp width.
- Dark theme required from day one; the card image sits on a neutral surface in
  both themes.
- Card screen must be screenshot-friendly: the share export is a rendered
  Compose bitmap, not a server image.

---

## 10. Cost envelope

| Item | Launch (season 1, 90 cards) | Notes |
|---|---|---|
| Image generation | ~$4 | or $0 with local Flux.1-schnell |
| LLM sentences + translation | ~$2 | |
| Supabase | $0 | free tier |
| Storage/CDN | $0 | ~10 MB per season |
| Play Console | $25 one-time | |
| **Total to launch** | **< $35** | |

Trigger to move off free tiers: >40k MAU or >1 GB monthly egress.

---

## 11. Definition of done for any feature

- Compiles with no warnings introduced.
- ViewModel has unit tests for state transitions.
- Works offline for cached content, degrades with a visible message otherwise.
- Strings extracted to `strings.xml` (English) and `values-bn/strings.xml`.
- No secrets in the repo; Supabase anon key in `local.properties`, read via
  BuildConfig.
- `PROJECT_STATE.md` updated in the same commit.
