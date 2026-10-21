# Kickoff — tell Claude Code in the folder that holds this file: `Run PROMPT.md`

Follow CLAUDE.md. Work in phases. At each ⛔ show the outputs and wait for my "go".

## Inputs
- Brief: XA Bank Time Deposit refactoring kata, the repo's `README.md`. Recruiter email: fork into my GitHub; deadline Wed 21 Oct 2026 13:00 UK; I email the public repo link; if accepted, a 1 h live session adds requirements; AI encouraged.
- Stack: Java 17 + Maven wrapper (no local Maven) · Spring Boot 3 · PostgreSQL + Flyway · Testcontainers · contract-first OpenAPI + Swagger UI.
- Time-box: 10 h of work over two days.
- Starter code: `java/`. Its only test asserts nothing; replacing it with characterization tests is approved.
- Remote: `https://github.com/<<<my GitHub user>>>/time-deposit-take-home-kata` (public fork). On a certificate error: `git -c http.sslBackend=schannel`.
- Company context (README next steps and REVIEW_NOTES only, never requirements): Ikigai Digital, UK consultancy building and modernising digital banks on thin-core platforms (Thought Machine Vault, 10x), event-driven; "think big, start small". In code, only zero-scope choices: banking vocabulary, immutable records.
- AI policy: required and graded (brief §6) → Co-Authored-By trailer: keep · README AI note: yes, detail in `docs/ai/README.md`.

## Time-box
- Count work time, not wall-clock: log `date` in the REVIEW_NOTES time log at each phase start, each ⛔, and when I say "pause" or "resume". Show elapsed / time-box in every report.
- Budget: Phase 0 10% · 1 15% · 2 50% · 3 10% · 4 15%.
- At 80% elapsed, when a phase overruns its budget by half, or on Wed 21 Oct at 09:00 UK: stop, report, and propose cuts in this order: screenshots (manual steps instead) → mutation testing → TRACEABILITY folded into TICKET → Phase 3 pass (per-AC refactors stay) → short SOLID map.
- Never cut: TDD per AC, parameterized tests, the test-only review, refactor commits, `<check>` and CI green, money correctness, README run + assumptions.

## Phase 0 — Refine (no code) ⛔
If missing, clone my fork into `<slug>/` (the repo name). Read the brief and starter code in full. Output ≤80 lines:
1. Brief constraints (deliverables, limits, evaluation criteria). They override CLAUDE.md and the toolchain.
2. Ticket IKG-1: story · AC-1…n in Given/When/Then, each quoting its brief sentence · out-of-scope · estimate in hours vs time-box.
3. Questions, each with a default assumption `A-n`; mark the 1–2 worth sending to the company.
4. Test list: AC · test name in the stack's convention · cases (happy / boundary) · level (unit, slice or end-to-end). Fix the test style once: naming, nesting, fixtures, test client.
5. Design: each type with a one-line responsibility; the two endpoints (method · path · status · body) and the tables. Name a SOLID principle only where it drove a split. List what is deliberately not abstracted.
6. Toolchain, versions pinned: test + assertion libs, formatter/linter, coverage, optional mutation. Evidence via `npx playwright` (no project dependency).
7. Starter-code smells (smell · location · refactoring), ranked by impact.
8. Risks: money maths, data integrity, API security — each with its control or an assumption `A-n`.

## Phase 1 — Scaffold ⛔
In `<slug>/`:
- Branch `feature/IKG-1-<slug>` off `main`. Commit `chore(IKG-1): init`: `.gitattributes` (`* text=auto eol=lf`, `*.cmd eol=crlf`), `.editorconfig`, `.gitignore` additions.
- Move the agent setup in: `CLAUDE.md` to the repo root, `PROMPT.md` and `CHANGELOG.md` to `docs/ai/`. The workspace `CLAUDE.md` becomes the single line `@<slug>/CLAUDE.md`. Commit `chore(IKG-1): add AI agent setup`.
- Maven wrapper (generated with a pinned Maven fetched once to a temp dir), exec bit via `git update-index --chmod=+x mvnw`, formatter, linter, coverage gate (≥90% line+branch on the domain), all in `<check>`.
- CI: GitHub Actions on push + PR: checkout → setup with cache → `<check>` → upload reports. Pinned actions, `permissions: contents: read`, `distributionSha256Sum` in `maven-wrapper.properties`.
- One smoke test, `<check>` green. Fill Commands in CLAUDE.md. Commit `chore(IKG-1): scaffold`. Push and show the CI result.

## Phase 2 — TDD per AC (test-list order)
- First pin current behaviour with one labelled characterization net; commit `test(IKG-1): characterize <area>`. Then write one readable test per rule, refactor in small steps, then add behaviour with the CLAUDE.md TDD loop.
- Report per AC in ≤3 lines: cases · red reason · refactor (move – smell, or none: reason) · elapsed.
- ⛔ after the first AC. Then continue unless a stop trigger fires.

## Phase 3 — Refactor pass ⛔
- Review all code for the CLAUDE.md smells, unused abstractions, and the money, integrity and security rules. One smell per commit, `<check>` green each time.
- Test-only review, as the reviewer would do it: read every test file without the production code. Fix hidden values, logic, unclear names, duplicate rules and fixture drift, one smell per `test(IKG-1): …` commit; delete a test that repeats another's rule.
- If budget allows: mutation testing; a surviving mutant that shows a missing case gets a readable test.
- Report: smell · location · move · commit.

## Phase 4 — Docs, evidence, PR ⛔
1. `docs/TICKET.md`: final ticket, assumptions, estimate vs actual time.
2. `docs/TRACEABILITY.md`: AC · test · cases · red reason · status.
3. `README.md` at the repo root, ≤2 pages (kata brief moved to `docs/BRIEF.md`, linked): CI badge · run from fresh clone · trigger both endpoints in Swagger UI (URL, steps, sample) · assumptions · design tree · decisions & trade-offs · SOLID map (principle → class → why; where not applied) · quality numbers (tests, coverage, mutation, time vs estimate) · correctness, integrity and security (risk → control or assumption) · production next steps · link to `docs/ai/README.md`.
4. `docs/ai/README.md`, ≤1 page: tools and models with versions · how to reproduce (`claude` in the repo root, then `Run docs/ai/PROMPT.md`) · what each file does · which parts were AI-assisted, why, and what I decided or reviewed · what is not included (my private interview notes).
5. `docs/evidence/`: CI run, test and coverage reports, Swagger UI, `git log --oneline --graph`, mutation report if run. Screenshot HTML and terminal output (`--console=plain`, in `<pre>`) with `npx playwright screenshot` (first `npx playwright install chromium`). Can't capture one: say why, give manual steps.
6. `REVIEW_NOTES.md` (repo's parent folder) as my presentation script:
   - Opening (≤1 min): Why (what the brief and reviewer need) → How (refine → TDD → refactor → CI), with one example from the code.
   - STAR stories, each Result backed by a number (tests, coverage, commits, time): delivery · a design decision · a refactor · how I used AI and this prompt system.
   - Time log, decision log and red reasons (written during Phases 0–3).
   - Live-change prep: 3 extensions a reviewer is likely to ask for live, each with the first failing test and the class it touches.
   - Code tour: files to open in order (tests, core types, then `git log`).
   - 2–3-line STAR answers: why not X? · how would it scale? · where is SOLID not applied? · where did a test change the design? · concurrency and money precision? · more time? · how did you use AI?
   - One weakness to own, plus what I'd do next.
7. Run the CLAUDE.md Definition of Done and show each proof.
8. Push; open the PR with TICKET.md as body and base `<my user>/<slug>:main` (GitHub defaults a fork's PR to the upstream). Merge commit only after my go.
9. Final: `git log --oneline`, test count, coverage %, run command. Then the deck, from `PRESENTATION.md`.
