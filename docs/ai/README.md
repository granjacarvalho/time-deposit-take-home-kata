# AI-assisted development

## Tools

| Tool | Use |
|---|---|
| Claude Code (desktop app, Code tab), model Claude Opus 5.5 | The only coding agent: planning, code, tests, docs, git, CI checks |
| Its built-in shell, file and browser tools | Running Maven, Docker and git; reading Maven Central and GitHub for versions and CI status |
| No other agent framework, MCP server or plugin | Kept the setup small and reproducible |

## Setup: two files drive the agent

| File | Loaded | Role |
|---|---|---|
| [`CLAUDE.md`](../../CLAUDE.md) | Every turn, automatically | The rulebook: grading order, brief overrides, TDD loop, design, money, integrity and security rules, Definition of Done, "never → instead" |
| [`PROMPT.md`](PROMPT.md) | Once, at kickoff | The procedure: inputs, time-box, five phases (Refine → Scaffold → TDD per AC → Refactor → Docs/PR), each ending in a ⛔ stop for my approval |
| [`CHANGELOG.md`](CHANGELOG.md) | Never | How the setup evolved, from an audit before the brief arrived to the changes this brief forced |

Key guardrails in `CLAUDE.md`: never edit a test to make it pass; never commit unless `./mvnw verify` is green; show the failing assertion before writing code (red reasons are in [TRACEABILITY](../TRACEABILITY.md)); claim nothing without command output; stop and ask before any new type, dependency or tool outside the approved plan.

## Reproduce

1. Install Claude Code and clone this repo; Java 17+ and Docker running.
2. Run `claude` in the repo root: `CLAUDE.md` loads by itself.
3. Say `Run docs/ai/PROMPT.md`. Its inputs are already filled in for this brief, so the agent starts with Phase 0 and stops for approval.

## Where I decided, where the AI worked

| Area | AI did | I decided or did |
|---|---|---|
| Refine | Drafted ticket, ACs, assumptions A-1…A-10, test list, design; ran a probe showing exact maths would change results | Approved the plan and Spring Boot 4.1.1; forked the repo, installed Docker |
| Scaffold | Maven wrapper, build, OpenAPI contract, CI with pinned actions | Signed in to GitHub so pushes work |
| Characterization | Recorded 360 cases from the upstream code; proved they fail on a broken boundary | — |
| Refactor | Eight refactors, one smell per commit, golden master green after each | — |
| New behaviour | TDD per AC: schema, two endpoints, extension point | Approved after the first AC |
| Correctness, integrity, security | Precision bound, schema constraints, row lock (a real lost-update bug found by a red test), safe headers | Asked for this pass explicitly; chose to keep authentication as assumption A-11 |
| Docs | Drafted README, ticket, traceability, this page | Reviewed at the Phase 4 stop |

**Why AI here:** the work is rule-heavy and repetitive (360 cases, boundary tests, migrations, docs), where an agent is fast; the rulebook keeps it from gaming tests, and the stops keep design decisions with me.

## What the guardrails caught

- A drift test that passed against a stub, so it proved nothing; rewritten until it failed first.
- Concurrent updates losing a month (1202.00 instead of 1204.00); fixed with a row lock.
- A commit that swallowed unrelated changes after a failed `git stash`; split before pushing.
- A wrong figure in the README draft ("~3%" instead of 0.03–0.17%); corrected against the probe output.

## What the guardrails missed

The reviewers rejected the submission on test quality: structure, clarity, consistency. They were right that the rules optimised for proof a machine can count (coverage, mutation score, red reasons) and never for a person reading the tests. The result was overlapping suites, logic in tests and Java-unusual names. The rules are rewritten in `CLAUDE.md` (Tests) and `PROMPT.md` (Phase 3 test-only review), and the tests were reworked to them afterwards: one readable test per rule with literal values, Java naming, the golden master labelled as a guard, behaviour instead of schema metadata, one API test class with one client and one fixture.

## Not included

My private interview notes and slides prompt, and a machine-specific note (a JDK truststore workaround for this laptop). None of them shapes the code.
