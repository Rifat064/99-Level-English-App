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

    const { card_id } = await req.json();
    if (!card_id) {
      return new Response(JSON.stringify({ error: "card_id is required" }), {
        status: 400,
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

    // Fetch user profile for enrolled_at
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

    const enrolledAt = new Date(profile.enrolled_at);
    const now = new Date();
    // Compute day_index: 1 for the first 24 hours, 2 for the next, etc.
    const diffTime = Math.max(0, now.getTime() - enrolledAt.getTime());
    const userDayIndex = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

    // Fetch the requested card
    const { data: card, error: cardError } = await supabaseClient
      .from("cards")
      .select("day_index, image_path, status")
      .eq("id", card_id)
      .single();

    if (cardError || !card) {
      return new Response(JSON.stringify({ error: "Card not found" }), {
        status: 404,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    if (card.status !== "published") {
      // Could also be 'reviewed' but not published yet
      return new Response(JSON.stringify({ error: "Card not available" }), {
        status: 403,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // Entitlement Check
    // Free window: Last 7 days, including today (userDayIndex - 7 to userDayIndex)
    // Note: Future cards (card.day_index > userDayIndex) are not accessible at all.
    if (card.day_index > userDayIndex) {
        return new Response(JSON.stringify({ error: "Forbidden: Future card" }), {
            status: 403,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
        });
    }

    const isFree = card.day_index > (userDayIndex - 7);
    let isEntitled = isFree;

    if (!isEntitled) {
      // Check active subscription
      const { data: subscription } = await supabaseClient
        .from("subscriptions")
        .select("status, expires_at")
        .eq("user_id", user.id)
        .eq("status", "active")
        .single();
        
      if (subscription) {
         if (subscription.expires_at) {
             const expiresAt = new Date(subscription.expires_at);
             if (expiresAt > now) {
                 isEntitled = true;
             }
         } else {
             isEntitled = true;
         }
      }
    }

    if (!isEntitled) {
      return new Response(JSON.stringify({ error: "Forbidden: Not entitled to this card. Subscription required." }), {
        status: 403,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // Client authenticated with service role to issue signed URL
    const serviceRoleClient = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
    );

    const { data: signedData, error: signError } = await serviceRoleClient
      .storage
      .from("card-images")
      .createSignedUrl(card.image_path, 600); // 10 minutes

    if (signError || !signedData) {
      return new Response(JSON.stringify({ error: "Failed to create signed URL" }), {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    return new Response(JSON.stringify({ signedUrl: signedData.signedUrl }), {
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
