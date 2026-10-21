# CLAUDE.md — Ikigai Digital take-home

## Goal
- Deliver the brief in `PROMPT.md` as a senior Product Engineer owning code, QA, CI and delivery. Every practice leaves something a reviewer sees, and reviewers read the tests first: they must read as the spec.
- Graded in order: TDD → parameterized tests → refactoring → SOLID. Also: naming, money correctness, delivery hygiene, every decision defensible in one sentence.
- Precedence: brief > my answers > this file > tool defaults.

## This brief: XA Bank time-deposit kata (overrides the sections below)
- Never change `org.ikigaidigital.TimeDeposit` or `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`: package, signature, behaviour, including the `double` maths and rounding. Pin them with a characterization net before any refactor; readable tests carry the intent.
- `BigDecimal` in new code, DB and API; the legacy `double` stays (README next step).
- Exactly two endpoints, defined in the OpenAPI contract. Hexagonal: no Spring in domain or use cases.
- No input validation or exception handling (not required).
- Assumptions `A-n` also as code comments where applied.
- Branches and PRs only in my fork, never in `ikigai-digital/*`.

## Commands (Git Bash; run from `java/`; all paths relative to the repo root)
```
./mvnw spotless:apply   # <format>: before <check>
./mvnw verify           # <check>: format, lint, all tests, coverage gate; needs Docker
./mvnw test             # <test>: unit tests only
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage   # <mutation>: CI gate, fails under 90%
```

## TDD loop (per AC, test-list order)
1. Red: the test compiles and fails an assertion for the expected reason. Show the failure and append `AC-n · red reason` to `REVIEW_NOTES.md`.
2. Green: minimal code (hard-coding, duplication allowed). `<check>` green. Commit `feat(IKG-1): AC-n <behaviour>`.
3. Refactor: check duplication, naming, long method, primitive obsession, type switches, test readability. One smell per commit, `<check>` green, `refactor(IKG-1): <move> – <smell>`. Nothing found → report "none: <reason>".
4. Log each decision in `REVIEW_NOTES.md`: decision · why · rejected alternative.
- After a context summary, resume from `git log --oneline` and `REVIEW_NOTES.md`.

## Stop and ask when
- a type, interface, dependency, tool or layer outside the approved Refine plan is needed
- the brief is ambiguous or an existing test looks wrong
- the time checkpoint in `PROMPT.md` is reached

## Design
- Simplest design that satisfies the brief; no layer, framework or tool it doesn't ask for.
- Abstract on the 2nd–3rd variant; an interface needs ≥2 implementations or a real test seam.
- SOLID only where it removes a real reason to change: SRP rules/orchestration/I/O · OCP new variant = new class, once variants exist · LSP no "unsupported" overrides · ISP small role interfaces · DIP domain owns its ports, wired by hand.
- Domain names. Immutable value objects validated on construction, only for brief concepts with invariants.
- Money: `BigDecimal` with explicit scale + `RoundingMode`. Document the formula, the rounding mode and when it rounds. Where exact maths is not allowed, test the error bound.
- Integrity: rules the data must always obey also live in the schema (NOT NULL, FK, CHECK, precision). One transaction per use case; lock rows that are read, changed and written. Never edit an applied migration; add the next one.
- Security: parameterized SQL only; no stack traces or internals in responses; `Cache-Control: no-store` on financial data; no secrets in the repo; no endpoint, actuator or debug surface beyond the brief. Authentication is implemented or written down as an assumption, never silent.
- Never return null.
- Small methods, one abstraction level, comments only for *why*.

## Tests (a reviewer reads them first: write for the reader, not for counts)
- One behaviour per test, understood in 10 seconds. Arrange, act and assert visible in the test body; inputs and expected values are literals in the test, not hidden in helpers or constants.
- Names follow the stack's convention (Java: camelCase method plus a `@DisplayName` sentence, or `@Nested` Given/When/Then). AC ids live in TRACEABILITY, not in names.
- One test per rule; no suites that repeat each other. Test count, coverage and mutation score are checks, never goals.
- No logic in tests: no loops, random data, computed expected values, reflection or parsing.
- Parameterize only boundary rows of one rule: at most ~8 named rows, never generated.
- Legacy code: a characterization net is one labelled regression guard, not the spec; shrink it once readable tests cover each rule.
- Pyramid: unit tests for rules and use cases (in-memory ports); slice tests for adapters (`@WebMvcTest`, `@JdbcTest`); one or two end-to-end tests with Testcontainers.
- Test behaviour, not framework or schema internals: prove a rule by a rejected write or a response, not by reading metadata.
- One fixture style and one test client per suite; shared setup in one place; close what you open.
- Money tests cover each boundary and rounding trap. Mock only owned ports, never value objects.
- Edges the brief doesn't state are assumptions `A-n`, implemented only after approval.
- Coverage ≥90% line+branch on the domain is a floor; mutation testing finds missing cases, it never excuses an unreadable test.

## Git
- Repo = clone of my fork in `<slug>/`. `main` + `feature/IKG-1-<slug>`. Commits `feat|refactor|test|ci|docs|chore(IKG-1): …`; one AC per `feat`, one smell per `refactor`.
- Merge the PR with a merge commit: history is the TDD evidence.
- Commit trailer and README AI note follow the AI policy in `PROMPT.md`.
- The agent setup is a deliverable: `CLAUDE.md` at the repo root, `PROMPT.md` and `CHANGELOG.md` in `docs/ai/`. `REVIEW_NOTES.md` and `PRESENTATION.md` stay private, in the repo's parent folder.

## Definition of Done (item — proof)
- Every AC has a named passing test — TRACEABILITY + test report
- Tests read as the spec — Phase 3 test-only review: one behaviour each, literal values, no logic, one fixture style
- Legacy behaviour pinned first — characterization commit before the first refactor in `git log`
- Exactly two endpoints — paths in the OpenAPI contract
- Fresh clone → `<check>` green — clone to a temp dir, output
- CI green — run link
- Coverage gate met, lint zero warnings — `<check>` output
- No float money outside the legacy maths — grep `float|double` in main source: only the calculator, the `interest` rules it was split into, and `Money.fromLegacy`
- No unused abstraction — interfaces with implementation counts
- Correctness, integrity, security reviewed — README section: each risk with its control or assumption
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
- Default locale, timezone, line separator or clock → explicit or injected.
- Claim a result → show the command output.
- Fake or edit evidence → capture it, or give manual steps.
- Commit secrets or generated reports (except `docs/evidence/`) → `.gitignore`.

## Style
Terse. Code > prose. One-line trade-offs. Challenge me when a simpler or safer option exists.
