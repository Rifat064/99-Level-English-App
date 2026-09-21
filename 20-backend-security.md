---
name: Backend, security, billing
description: Rules for Supabase SQL, RLS, edge functions, and anything touching money or entitlement.
activation: glob
globs: ["supabase/**", "**/core-network/**", "**/core-billing/**", "**/*.sql"]
---

# Backend, security, billing

This is the area where a mistake costs money or leaks paid content. Slow down.

## Migrations

- Migrations are append-only and numbered: `0001_init.sql`, `0002_rls.sql`, …
- Never edit a migration that has been applied to the hosted project. Write a
  new one.
- Every migration must be idempotent-safe to re-run on a fresh database and
  must include a matching `-- down` section as a comment.
- No `DROP TABLE` or `DROP COLUMN` without explicit approval in chat.

## Row Level Security

- RLS is **enabled on every table**. A table without a policy is a leak, not a
  default-deny.
- User-owned tables (`profiles`, `user_progress`, `quiz_attempts`,
  `subscriptions`): read and write only where `user_id = auth.uid()`.
- `subscriptions.status`, `expires_at`, `purchase_token`: **no client write
  policy at all**. Service role only.
- `words`: public read. `cards`: public read of metadata; `image_path` is never
  exposed directly to the client.
- Every new policy ships with a test in `supabase/tests/` proving that user A
  cannot read or write user B's row. A policy without a test is not done.

## Edge functions

- Functions validate the JWT and derive the user id from it. Never accept a
  user id from the request body.
- `get-daily-card` computes `day_index` from `profiles.enrolled_at` and server
  time. Never from a client-supplied date.
- `get-signed-image` checks entitlement before signing, and signs for 10
  minutes maximum.
- `verify-purchase` validates the token against the Google Play Developer API
  server-side. A client-reported purchase is never sufficient.
- All functions return typed errors with a code, not raw stack traces.
- Log user ids, never tokens, emails, or purchase tokens.

## Billing

- Feature code depends on the `BillingProvider` interface only. No feature
  module imports the Play Billing library directly.
- Entitlement is read from `subscriptions` on the server, cached locally with a
  short TTL, and re-checked on app resume.
- Handle every state: purchased, pending, cancelled but active until
  `expires_at`, grace period, on hold, refunded, restored on a new device.
- Acknowledge purchases within Play's window or they auto-refund. This is a
  common and expensive bug — write the acknowledgement path first.
- Test with the fake provider and with Play's test SKUs before touching real
  products.

## Secrets

- `SUPABASE_URL` and `SUPABASE_ANON_KEY` may reach the app via `BuildConfig`.
- The service-role key, the Play service-account JSON, and any LLM or image API
  key exist **only** in Supabase function secrets or the owner's local
  environment. If you find one in a tracked file, stop everything and report it.
