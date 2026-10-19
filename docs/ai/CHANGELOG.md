# CHANGELOG — agent system
## 2026-10-18 — redesign after audit (45 findings, 15 high)
- One source of truth: CLAUDE.md = rules, PROMPT.md = procedure. CONTEXT.v2.md deleted (85% duplicate, referenced under a wrong name).
- PROMPT phases renumbered 0–4. `AC-n` is the only requirement ID; Phase 0 drafts ticket, assumptions `A-n`, test list, design and toolchain.
- Every DoD item has a producing phase and a proof; every Never-rule has an "instead".
- Added time-box control (start time, phase budgets, 80% checkpoint, cut order) and explicit stop triggers.
- Added determinism and Windows rules: explicit Locale/ZoneId, `\n`, platform Clock, `.gitattributes`, gradlew exec bit, native runner instead of make.
- Repo moves to a subfolder; Remote/Submission/AI-policy inputs; merge commit or bundle so the TDD history reaches the reviewer.
- One-time content moved out of CLAUDE.md: always-loaded tokens 1,102 → ~970; kickoff load 2,558 → ~2,070.
- Added PRESENTATION.md: debrief-deck prompt with my profile; each STAR slide pairs with a real past project.
## 2026-10-19 — senior review (12 of 15 findings applied)
- Precedence rule: brief > my answers > CLAUDE.md > tool defaults; Phase 0 lists brief constraints first.
- Starter-code path: characterization tests before any refactor (refactoring katas).
- Red reasons logged at red time; resume from git log + REVIEW_NOTES after a context summary.
- Readable parameterized case names; `<format>` command; commands run from `<slug>/`.
- CI hardening (pinned actions, read-only permissions, wrapper validation); screenshots now cut before mutation.
- REVIEW_NOTES gains live-change prep and a code tour; estimate vs actual time in TICKET and README.
- Presentation: engineering first, AI as the how; past-project lines on screen only on STAR slides.
## 2026-10-19 — brief received: XA Bank time-deposit kata
- CLAUDE.md brief block: legacy `TimeDeposit`/`updateBalance` frozen incl. `double` maths; `BigDecimal` in new code; exactly two endpoints; hexagonal; no validation; assumptions as code comments; PRs only in the fork.
- Brief §6 makes the agent setup a deliverable: CLAUDE.md, PROMPT.md, CHANGELOG.md move into the repo in Phase 1; workspace CLAUDE.md becomes an `@` import. REVIEW_NOTES.md and PRESENTATION.md stay private.
- Fork clone replaces `git init`; Maven wrapper replaces Gradle; paths relative to the repo root.
- Inputs filled: Java 17, Spring Boot 3, PostgreSQL + Flyway, Testcontainers, contract-first OpenAPI; 10 h time-box.
- Time-box counts work time across two days, with a Wed 09:00 UK cut-off before the 13:00 deadline.
- Phase 4 adds Swagger trigger steps, `docs/ai/README.md` (tools, reproduce, what was AI-assisted) and the fork-PR base trap.
- Trimmed to stay in budget: CLAUDE.md 1,194 tok, PROMPT.md 1,500 tok (words × 1.3).
## 2026-10-19 — money, integrity and security made explicit
- CLAUDE.md Design gains three rules: money (document formula and rounding; test the error bound where exact maths is not allowed), integrity (schema constraints, one transaction per use case, row locks, never edit an applied migration), security (parameterized SQL, no internals in responses, no-store, minimal surface, authentication never silent).
- Tests and DoD follow: money and integrity tests; a README section mapping each risk to its control or assumption.
- PROMPT.md: Phase 0 lists risks, Phase 3 reviews them, Phase 4 README reports them. CLAUDE.md now ~1,350 tok (over the 1,200 budget, accepted for these rules).
## 2026-10-19 — platform and QA review
- CLAUDE.md: `<mutation>` is now a CI gate (fails under 90%), not optional.
