# CLAUDE.md — Ikigai Digital take-home

## Goal
- Deliver the brief in `PROMPT.md` as a senior Product Engineer owning code, QA, CI and delivery. Every practice must leave something a reviewer sees.
- Graded in order: TDD → parameterized tests → refactoring → SOLID. Also: naming, money correctness, delivery hygiene, every decision defensible in one sentence.
- Precedence: brief > my answers > this file > tool defaults.

## This brief: XA Bank time-deposit kata (overrides the sections below)
- Never change `org.ikigaidigital.TimeDeposit` or `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`: package, signature, behaviour, including the `double` maths and rounding. Pin them with characterization tests before any refactor.
- `BigDecimal` in new code, DB and API; the legacy `double` stays (README next step).
- Exactly two endpoints, defined in the OpenAPI contract. Hexagonal: no Spring in domain or use cases.
- No input validation or exception handling (not required).
- Assumptions `A-n` also as code comments where applied.
- Branches and PRs only in my fork, never in `ikigai-digital/*`.

## Commands (fill at scaffold; Git Bash; run from `java/`; all paths relative to the repo root)
```
<format>     # auto-format; run before <check>
<check>      # lint + tests + coverage gate; before every commit
<test>       # tests only
<mutation>   # optional
```

## TDD loop (per AC, test-list order)
1. Red: the test compiles and fails an assertion for the expected reason. Show the failure lines and append `AC-n · red reason` to `REVIEW_NOTES.md`.
2. Green: minimal code (hard-coding, duplication allowed). `<check>` green. Commit `feat(IKG-1): AC-n <behaviour>`.
3. Refactor: check duplication, naming, long method, primitive obsession, type switches, test readability. One smell per commit, `<check>` green, `refactor(IKG-1): <move> – <smell>`. Nothing found → report "none: <reason>".
4. Log each decision in `REVIEW_NOTES.md`: decision · why · rejected alternative.
- After a context summary, resume from `git log --oneline` and `REVIEW_NOTES.md`.

## Stop and ask when
- a type, interface, dependency, tool or layer outside the approved Refine plan is needed
- the brief is ambiguous or an existing test looks wrong
- the time checkpoint in `PROMPT.md` is reached

## Design
- Simplest design that satisfies the brief. No DB, HTTP, CLI, DI framework or extra layers unless asked.
- Abstract on the 2nd–3rd variant; an interface needs ≥2 implementations or a real test seam.
- SOLID only where it removes a real reason to change: SRP rules/orchestration/I/O · OCP new variant = new class, once variants exist · LSP no "unsupported" overrides · ISP small role interfaces · DIP domain owns its ports, wired by hand.
- Domain names. Immutable value objects validated on construction, only for brief concepts with invariants.
- Money: `BigDecimal` with explicit scale + `RoundingMode`; one documented currency constant.
- Never return null.
- Small methods, one abstraction level, comments only for *why*.

## Tests
- Tests are the spec: `should_<outcome>_when_<condition>`, AC id in `@DisplayName`/describe. AAA, no logic, deterministic.
- Each AC → ≥1 test through the public API, tagged `AC-n`. Extra unit tests only for value-object invariants.
- Parameterize when ≥2 cases share one assertion shape; valid and invalid cases in separate tests. Give cases readable names (`@ParameterizedTest(name = …)` or CSV headers) so the test report reads as the spec.
- Edges the brief doesn't state are assumptions `A-n`, implemented only after approval.
- Mock only owned ports, never value objects.
- Coverage ≥90% line+branch on the domain is a floor; prefer killing mutants.

## Git
- Repo = clone of my fork in `<slug>/`. `main` + `feature/IKG-1-<slug>`. Commits `feat|refactor|test|ci|docs|chore(IKG-1): …`; one AC per `feat`, one smell per `refactor`.
- Merge the PR with a merge commit: history is the TDD evidence.
- Commit trailer and README AI note follow the AI policy in `PROMPT.md`.
- The agent setup is a deliverable: `CLAUDE.md` at the repo root, `PROMPT.md` and `CHANGELOG.md` in `docs/ai/`. `REVIEW_NOTES.md` and `PRESENTATION.md` stay private, in the repo's parent folder.

## Definition of Done (item — proof)
- Every AC has a named passing test — TRACEABILITY + test report
- Legacy behaviour pinned first — characterization commit before the first refactor in `git log`
- Exactly two endpoints — paths in the OpenAPI contract
- Fresh clone → `<check>` green — clone to a temp dir, output
- CI green — run link
- Coverage gate met, lint zero warnings — `<check>` output
- No float money — grep `float|double` in main source: only the two legacy classes
- No unused abstraction — interfaces with implementation counts
- Refactor commits name smell + move — `git log --oneline`
- Evidence captured — `docs/evidence/`, or manual steps
- TICKET, TRACEABILITY, README (Swagger steps), `docs/ai/README.md` written — files
- Agent setup committed; no private notes or generated reports — `git ls-files`

## Never → instead
- Edit or delete a test to make it pass → fix the code, or stop and ask.
- Commit red, skip tests, lower the gate → `<check>` before every commit.
- Mix refactoring with behaviour change → separate commits.
- Squash, rebase or amend pushed commits → merge commit.
- Add an unapproved dependency → stop and ask.
- Float/double money → `BigDecimal` or minor units.
- Default locale, timezone, line separator or clock → explicit or injected.
- Claim a result → show the command output.
- Fake or edit evidence → capture it, or give manual steps.
- Commit secrets or generated reports (except `docs/evidence/`) → `.gitignore`.

## Style
Terse. Code > prose. Diffs, not full files. One-line trade-offs. Challenge me when a simpler or safer option exists.
