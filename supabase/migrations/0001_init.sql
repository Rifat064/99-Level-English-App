CREATE TABLE words (
  id bigserial primary key,
  word text unique not null,
  pos text,                        -- noun / verb / adj
  bangla text not null,
  english_gloss text,
  exam_tag text,                   -- 'BCS 35', 'Janata Bank 2019'
  exam_category text,              -- BCS | BANK | IELTS | GOVT | GENERAL
  difficulty smallint,             -- 1..5
  frequency_rank int,
  created_at timestamptz default now()
);

CREATE TABLE cards (
  id bigserial primary key,
  day_index int unique not null,   -- 1..N, the global curriculum position
  season int not null,
  word_a_id bigint references words(id),
  word_b_id bigint references words(id),
  sentence_en text not null,
  sentence_bn text not null,
  bubble_text text,                -- optional short line for the overlay
  image_path text not null,        -- storage key, not a public URL
  image_blurhash text,
  status text not null,            -- draft | reviewed | published
  published_at timestamptz
);

CREATE TABLE profiles (
  id uuid primary key references auth.users(id),
  display_name text,
  enrolled_at timestamptz not null default now(),
  words_per_day smallint default 2,   -- 2 | 4 | 6
  timezone text default 'Asia/Dhaka',
  notify_hour smallint default 8,
  streak_count int default 0,
  longest_streak int default 0,
  last_completed_day int default 0
);

CREATE TABLE user_progress (
  user_id uuid references auth.users(id),
  card_id bigint references cards(id),
  seen_at timestamptz,
  completed boolean default false,
  bookmarked boolean default false,
  self_rating smallint,            -- 1 easy .. 3 hard, feeds review deck
  primary key (user_id, card_id)
);

CREATE TABLE quiz_attempts (
  id bigserial primary key,
  user_id uuid references auth.users(id),
  week_index int not null,
  score smallint,
  total smallint,
  answers jsonb,
  taken_at timestamptz default now(),
  unique (user_id, week_index)
);

CREATE TABLE subscriptions (
  user_id uuid primary key references auth.users(id),
  provider text not null,          -- play | sslcommerz | bkash
  product_id text,
  status text not null,            -- active | grace | expired | refunded
  purchase_token text,
  expires_at timestamptz,
  updated_at timestamptz
);
