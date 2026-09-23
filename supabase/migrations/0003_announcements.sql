CREATE TABLE global_announcements (
  id bigserial primary key,
  type text not null,               -- 'EVENT' or 'EDITORS_CHOICE'
  title text not null,              -- Banner title (e.g., "Upcoming Live Session!")
  message text not null,            -- Detailed description
  target_word_id bigint references words(id), -- Nullable, used for Editor's Choice
  action_url text,                  -- Nullable, link to a webpage or video
  is_active boolean default true,   -- Admin toggles this to turn it off
  created_at timestamptz default now()
);

-- RLS Policy: Public read-only
ALTER TABLE global_announcements ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Public read access for announcements" ON global_announcements FOR SELECT USING (true);
