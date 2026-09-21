import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const authHeader = req.headers.get("Authorization");
    if (!authHeader) {
      return new Response(JSON.stringify({ error: "Missing Auth Header" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // Client authenticated as the requesting user
    const supabaseClient = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_ANON_KEY") ?? "",
      { global: { headers: { Authorization: authHeader } } }
    );
    
    const { data: { user }, error: userError } = await supabaseClient.auth.getUser();
    if (userError || !user) {
      return new Response(JSON.stringify({ error: "Unauthorized" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // 1. Fetch user profile for enrolled_at
    const { data: profile, error: profileError } = await supabaseClient
      .from("profiles")
      .select("enrolled_at")
      .eq("id", user.id)
      .single();

    if (profileError || !profile) {
      return new Response(JSON.stringify({ error: "Profile not found" }), {
        status: 404,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // 2. Compute day_index
    const enrolledAt = new Date(profile.enrolled_at);
    const now = new Date();
    // Use Math.max to prevent negative day_index if clock is skewed slightly before enrolled_at
    const diffTime = Math.max(0, now.getTime() - enrolledAt.getTime());
    const userDayIndex = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

    // 3. Fetch the requested card
    // We fetch the card that matches the userDayIndex.
    const { data: card, error: cardError } = await supabaseClient
      .from("cards")
      .select("*")
      .eq("day_index", userDayIndex)
      .eq("status", "published")
      .single();

    if (cardError || !card) {
      // If card doesn't exist for this day (e.g. they reached the end of content)
      return new Response(JSON.stringify({ error: "Card not found for today" }), {
        status: 404,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // 4. Fetch the words associated with this card
    const { data: words, error: wordsError } = await supabaseClient
      .from("words")
      .select("*")
      .in("id", [card.word_a_id, card.word_b_id]);

    if (wordsError || !words || words.length === 0) {
      return new Response(JSON.stringify({ error: "Words not found for this card" }), {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const wordA = words.find((w: any) => w.id === card.word_a_id) || null;
    const wordB = words.find((w: any) => w.id === card.word_b_id) || null;

    // 5. Combine and return payload
    const payload = {
        day_index: userDayIndex,
        card: card,
        word_a: wordA,
        word_b: wordB
    };

    return new Response(JSON.stringify(payload), {
      status: 200,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (err) {
    return new Response(JSON.stringify({ error: err.message }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  }
});
