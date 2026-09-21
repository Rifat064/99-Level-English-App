BEGIN;
CREATE EXTENSION IF NOT EXISTS pgtap;

SELECT plan(2);

-- Setup: Create dummy users
INSERT INTO auth.users (id, email) VALUES 
  ('11111111-1111-1111-1111-111111111111', 'userA@example.com'),
  ('22222222-2222-2222-2222-222222222222', 'userB@example.com');

-- Setup: Create a dummy card
INSERT INTO cards (id, day_index, season, sentence_en, sentence_bn, image_path, status) 
VALUES (1, 1, 1, 'Hello', 'Namaskar', 'path', 'published');

-- Insert progress for User A (as postgres / service role)
INSERT INTO user_progress (user_id, card_id, completed) 
VALUES ('11111111-1111-1111-1111-111111111111', 1, true);

-- Test 1: User A can see their own progress
SET LOCAL ROLE authenticated;
SET LOCAL "request.jwt.claim.sub" TO '11111111-1111-1111-1111-111111111111';

SELECT results_eq(
  $$ SELECT completed FROM user_progress WHERE card_id = 1 $$,
  $$ VALUES (true) $$,
  'User A can read their own user_progress'
);

-- Test 2: User B cannot see User A's progress
SET LOCAL "request.jwt.claim.sub" TO '22222222-2222-2222-2222-222222222222';

SELECT is_empty(
  $$ SELECT completed FROM user_progress WHERE card_id = 1 $$,
  'User B cannot read User A''s user_progress'
);

SELECT * FROM finish();
ROLLBACK;
