# RULES.md

Index of the agent rules in this repository, and how to install them in
Antigravity and Android Studio.

---

## What lives where

```
your-project/
├── AGENTS.md                      # core rules, read by BOTH IDEs — start here
├── RULES.md                       # this file (human index, not read by agents)
├── .aiexclude                     # Android Studio: files Gemini may never read
├── .agents/
│   └── rules/                     # Antigravity workspace rules
│       ├── 00-core-workflow.md    # Always On
│       ├── 10-kotlin-compose.md   # Glob: **/*.kt, **/*.kts
│       ├── 20-backend-security.md # Glob: supabase/**, core-network/**, core-billing/**
│       └── 30-content-pipeline.md # Glob: pipeline/**, **/*.py
├── DESIGN_AND_SCOPE.md
├── BUILD_PLAN.md
└── PROJECT_STATE.md
```

`AGENTS.md` holds the rules that must apply no matter which tool is open. The
`.agents/rules/` files are scoped supplements so the agent is not carrying
Python pipeline rules while editing a Composable.

---

## Antigravity setup

1. Open the **Customizations** panel from the agent panel menu → **Rules**.
2. Antigravity reads workspace rules from `.agents/rules/` at the workspace or
   Git root. (The older `.agent/rules/` path still works; if your version was
   created before the rename, just duplicate the folder.)
3. Set each file's activation mode in the Rules UI if the frontmatter in the
   file is not picked up automatically:

   | File | Mode | Scope |
   |---|---|---|
   | `00-core-workflow.md` | Always On | — |
   | `10-kotlin-compose.md` | Glob | `**/*.kt`, `**/*.kts` |
   | `20-backend-security.md` | Glob | `supabase/**`, `**/core-network/**`, `**/core-billing/**`, `**/*.sql` |
   | `30-content-pipeline.md` | Glob | `pipeline/**`, `**/*.py` |

4. Keep each rule file under **12,000 characters** — that is Antigravity's
   per-file limit. All four files here are well inside it. If you grow one,
   split it rather than trimming the hard rules.
5. If a rule seems ignored, the cause is almost always an activation mismatch,
   not the content: a Glob rule only fires on matching files, and a Manual rule
   has to be mentioned by name.
6. Rule files can reference repo files with `@` paths, resolved relative to the
   rule file. `00-core-workflow.md` uses `@../../AGENTS.md` for this reason.

### Suggested prompt to open a session

```
Read AGENTS.md, DESIGN_AND_SCOPE.md, BUILD_PLAN.md and PROJECT_STATE.md.
Tell me the current step and your plan for it. Do not write code yet.
```

Then approve the plan before letting it run. Agentic IDEs go wrong fastest in
the first thirty seconds, and that is the cheapest place to stop them.

---

## Android Studio setup

1. Gemini Agent Mode reads `AGENTS.md` from the project root — no extra
   configuration needed. It does **not** read `.agents/rules/`, so anything
   that must always hold is in `AGENTS.md` by design.
2. `.aiexclude` is already in place. It uses `.gitignore` syntax and blocks
   Gemini from reading keystores, `local.properties`, service-account JSON, and
   the pipeline cache. It applies to its own directory and below, and it can be
   committed so the whole team gets it.
3. Enable project context sharing (Settings → Gemini) or the agent will work
   blind.
4. Use Android Studio for the things it is actually better at: Gradle sync
   errors, Compose previews, Layout Inspector, profiling, Logcat triage, device
   testing, and running the release build. Use Antigravity for multi-file
   feature work.

---

## Division of labour I'd suggest

| Task | Tool |
|---|---|
| Implementing a `BUILD_PLAN.md` step across modules | Antigravity |
| SQL migrations and edge functions | Antigravity |
| Pipeline scripts | Antigravity |
| Gradle/dependency resolution errors | Android Studio |
| Compose UI tuning against a live preview | Android Studio |
| Crash and Logcat investigation | Android Studio |
| Release build, signing, Play upload | Android Studio |

Do not run both agents on the same branch at once. They will overwrite each
other and `PROJECT_STATE.md` will lie to you.

---

## Maintaining these rules

- A rule earns its place by having prevented a real bug. If a rule has never
  fired, delete it — long rule files get skimmed by models the same way they do
  by people.
- When the agent makes the same mistake twice, that is the signal to add a
  rule, not to repeat yourself in chat.
- Rules describe constraints and workflow. Product facts belong in
  `DESIGN_AND_SCOPE.md`; progress belongs in `PROJECT_STATE.md`. Keeping them
  separate is what stops the rules file from rotting.
