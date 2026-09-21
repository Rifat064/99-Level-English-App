-- Enable RLS on all tables
ALTER TABLE words ENABLE ROW LEVEL SECURITY;
ALTER TABLE cards ENABLE ROW LEVEL SECURITY;
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE quiz_attempts ENABLE ROW LEVEL SECURITY;
ALTER TABLE subscriptions ENABLE ROW LEVEL SECURITY;

-- words: public read
CREATE POLICY "Words are readable by everyone" 
  ON words FOR SELECT USING (true);

-- cards: public read
-- Note: 'image_path' is protected by Storage RLS and signed URLs via Edge Function.
CREATE POLICY "Cards are readable by everyone" 
  ON cards FOR SELECT USING (true);

-- profiles: user may read and write only their own
CREATE POLICY "Users can read own profile" 
  ON profiles FOR SELECT USING (auth.uid() = id);
CREATE POLICY "Users can insert own profile" 
  ON profiles FOR INSERT WITH CHECK (auth.uid() = id);
CREATE POLICY "Users can update own profile" 
  ON profiles FOR UPDATE USING (auth.uid() = id);

-- user_progress: user may read and write only their own
CREATE POLICY "Users can read own progress" 
  ON user_progress FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own progress" 
  ON user_progress FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own progress" 
  ON user_progress FOR UPDATE USING (auth.uid() = user_id);
CREATE POLICY "Users can delete own progress" 
  ON user_progress FOR DELETE USING (auth.uid() = user_id);

-- quiz_attempts: user may read and write only their own
CREATE POLICY "Users can read own quiz attempts" 
  ON quiz_attempts FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own quiz attempts" 
  ON quiz_attempts FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own quiz attempts" 
  ON quiz_attempts FOR UPDATE USING (auth.uid() = user_id);

-- subscriptions: user may read their own.
-- Writes are done via service-role in edge functions which bypasses RLS.
CREATE POLICY "Users can read own subscriptions" 
  ON subscriptions FOR SELECT USING (auth.uid() = user_id);
