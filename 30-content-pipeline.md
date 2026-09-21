---
name: Content pipeline
description: Rules for the offline Python scripts that build cards, sentences, translations, and images.
activation: glob
globs: ["pipeline/**", "**/*.py"]
---

# Content pipeline

Runs on the owner's machine only. Nothing here ships in the APK, and the app
never calls an LLM or image API at runtime.

## Structure

Scripts are numbered and single-purpose: `01_curate` → `02_pair` →
`03_sentence` → `04_translate` → **05 human gate** → `06_image` →
`07_optimize` → `08_upload`.

- Each script reads a file and writes a file. No script does two stages.
- Every script is re-runnable: if an output row already exists, skip it unless
  `--force` is passed. API calls cost money; never regenerate blindly.
- Every API call is cached to `pipeline/.cache/` keyed by a hash of the prompt.
- Log the running cost estimate at the end of each script.

## The human gate is blocking

`05_review.csv` is edited by a person. No script may:

- write to `05_review.csv` after `04_translate` has produced it,
- promote a row past the gate automatically,
- set `cards.status` to anything above `reviewed`.

Publishing is a manual action by the owner. Machine-translated Bangla reaching
users is treated as a P0 bug.

## Sentence generation

- Exactly two target words per sentence, both used naturally, not forced.
- The sentence must describe a **concrete, drawable scene** — a person doing
  something in a place. Abstract sentences produce useless images.
- 12–22 words. It has to be memorable, not literary.
- Reject and regenerate if either word appears in a form the exam would not
  test, or if the two words are near-synonyms.

## Image generation

- Prompt template is fixed in `DESIGN_AND_SCOPE.md` §7 and must include the
  negative instruction against text, letters, words, signage, and captions.
- Never ask the model to render the sentence, a speech bubble, or any caption.
- Output 1024×1024, then WebP at roughly 100 KB, then compute a blurhash.
- Any image containing legible text is rejected and regenerated, not shipped.

## Data hygiene

- `words_master.csv` is read-only input. Never modify it in place; write
  derived files.
- Validate on load: no duplicate words, no empty Bangla, exam tags match the
  allowed set, difficulty in 1..5. Fail loudly on a bad row with its line
  number.
- Word pairs must not repeat across a season, and no word may appear twice in
  the whole catalog.

## Style

- Python 3.11, type hints on every function, `ruff` clean.
- `argparse` with `--dry-run` on any script that spends money or writes to
  Supabase. `--dry-run` is the default for `06_image` and `08_upload`.
- No secrets in code — read from environment variables, fail with a clear
  message if missing.
