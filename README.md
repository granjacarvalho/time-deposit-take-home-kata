# XA Bank Time Deposits — refactoring kata (Java)

[![CI](https://github.com/granjacarvalho/time-deposit-take-home-kata/actions/workflows/ci.yml/badge.svg)](https://github.com/granjacarvalho/time-deposit-take-home-kata/actions/workflows/ci.yml)

The junior developer's interest logic, refactored without changing a single result, behind exactly two REST endpoints on PostgreSQL. Brief: [docs/BRIEF.md](docs/BRIEF.md) · ticket and assumptions: [docs/TICKET.md](docs/TICKET.md) · AC → test map: [docs/TRACEABILITY.md](docs/TRACEABILITY.md).

## Run it

Needs **Java 17+** and **Docker** running. Maven comes with the wrapper. Commands are for Git Bash or any POSIX shell; on `cmd` use `mvnw.cmd`.

```bash
git clone https://github.com/granjacarvalho/time-deposit-take-home-kata.git
cd time-deposit-take-home-kata/java
./mvnw verify                 # format check, lint, 1,390 unit + 35 integration tests, coverage gate
./mvnw spring-boot:test-run   # app on :8080, PostgreSQL 17 in Docker, demo data loaded
```

## Trigger the endpoints with Swagger

1. Start the app with `./mvnw spring-boot:test-run` and open <http://localhost:8080/swagger-ui/index.html>. The UI shows the contract [`openapi.yaml`](java/src/main/resources/static/openapi.yaml).
2. **GET `/time-deposits`** → *Try it out* → *Execute*. Six demo deposits, e.g.
   ```json
   {"id":1,"planType":"basic","balance":1234567.00,"days":45,"withdrawals":[]}
   ```
3. **POST `/time-deposits/balance-updates`** → *Try it out* → *Execute* → `204`. One month of interest is applied to every deposit.
4. **GET** again: deposit 1 is now `1235595.81`, deposit 6 (`basic`, 18.00) is `18.01`, the 400-day student and 40-day premium deposits are unchanged. Each further POST applies one more month (A-3).

Without Swagger: `curl localhost:8080/time-deposits` and `curl -X POST localhost:8080/time-deposits/balance-updates`.

Swagger's response viewer drops trailing zeros (`1234567`); the raw body keeps two decimals (`1234567.00`), as `curl` shows. Screenshots: [docs/evidence](docs/evidence/README.md).

## Design

```
java/src/main/java/org/ikigaidigital
├── TimeDeposit, TimeDepositCalculator   legacy, same package and signatures; calculator now delegates to rules
├── Money, Withdrawal                    double → BigDecimal at the edges; read-only withdrawal record
├── interest/                            InterestRule (Strategy) · ExistingPlanInterest (30-day rule, rate / 12)
│                                        · Basic/Student/PremiumPlanInterest · InterestRules (default plans)
├── application/                         UpdateAllBalances · ListTimeDeposits · ports TimeDepositRepository, WithdrawalRepository
├── adapter/web/                         controller implementing the generated contract interface · security headers
├── adapter/persistence/                 JDBC adapters (JdbcClient, explicit SQL) · Flyway V1 tables, V2 constraints
└── config/                              Spring Boot app, use cases wired by hand
```

Hexagonal: domain and application have no Spring imports, enforced by `ArchitectureTest`. **A new plan** is one class implementing `InterestRule`, added to `InterestRules.briefPlans()`; the calculator is not edited.

## Interest maths

One month = `balance × annual rate / 12`, rounded **HALF_UP to cents**, added to the balance (A-12). The legacy code does this in `double` and rounds with `new BigDecimal(double)`. Exact decimal maths would change results (basic 18.00 earns 0.01 today, 0.02 exactly), which the brief forbids, so:

- 360 recorded cases pin today's results bit for bit (AC-1);
- `InterestPrecisionTest` pins the error: never more than **0.01 per month** from exact maths (a probe over 30.6M balances found no larger gap);
- everything new (DB, API, conversion) is `BigDecimal`, and balances are capped below 10^13, where a `double` still holds every cent.

## Correctness, integrity and security

| Risk | Control | Proof |
|---|---|---|
| Refactor changes a result | Golden master of 360 cases from the untouched upstream code | `TimeDepositCalculatorCharacterizationTest` |
| Floating-point money | `BigDecimal`/`NUMERIC(15,2)` outside the legacy maths; error bound tested | `InterestPrecisionTest`, `JdbcTimeDepositRepositoryIT` |
| Invalid rows from any writer | Schema: NOT NULL, FK, `balance >= 0`, `days >= 0`, `amount > 0`, max 13 integer digits | `SchemaIT` |
| Two updates at once lose a month | One transaction per request; update path locks rows (`SELECT … FOR UPDATE`) | `UpdateBalancesIT` (4 parallel calls) |
| List mixes two moments | Read-only `REPEATABLE READ` transaction | design decision |
| SQL injection | Parameterized SQL only; no request input reaches SQL | code review |
| Leaking internals or cached balances | Errors without trace/exception/message; `Cache-Control: no-store`, `nosniff` on every response | `ApiHardeningIT` |
| Unauthorized balance update | **Assumption A-11:** gateway authentication; update needs an operations role | contract description |
| Extra attack surface | Exactly two endpoints; no Actuator | `ApiSurfaceIT` (running app vs contract), `ApiHardeningIT` |
| Vulnerable dependencies or code | Dependabot (Maven, Actions) and CodeQL on every PR | `.github/` |

## Decisions and trade-offs

- **Keep legacy `double` maths** · the brief forbids behaviour changes · exact `BigDecimal` maths would change 0.03–0.17% of results (3,019 to 17,069 of 10M balances, by plan).
- **Contract first** (OpenAPI → generated interface) · the contract is the single source for code and Swagger · code-first annotations.
- **Plain JDBC, no JPA** · `TimeDeposit` has no no-arg constructor and the SQL is four statements · entity mapping layer.
- **Template Method under the Strategy** · removes the duplicated 30-day rule and `/ 12` · each plan restating them.
- **Pessimistic lock on update** · one statement, no schema change · optimistic version column with retries.
- **Integrity in the schema, not Java validation** · the brief excludes input validation and the DB guards every writer.
- **Not abstracted:** use-case interfaces (one implementation each), a plan-type enum (the string is the legacy contract), a `Money` type inside `TimeDeposit` (shared class).

## SOLID map

| Principle | Where | Why |
|---|---|---|
| SRP | `TimeDepositCalculator` (apply and round) vs `InterestRule`s (rates) vs adapters (I/O) | Rates, rounding and storage change for different reasons |
| OCP | `InterestRule` + `InterestRules` + `TimeDepositCalculator(List<InterestRule>)` | New plan = new class + one registry line (AC-6) |
| LSP | Every rule honours `monthlyInterest`; unknown plans use a no-interest rule, no `null` | No special cases at call sites |
| ISP | One small port per table: `TimeDepositRepository`, `WithdrawalRepository` | Partly: `ListTimeDeposits` sees the write methods of `TimeDepositRepository`; split read/write ports when a second writer appears |
| DIP | Ports in `application`, implemented by `adapter.persistence`, wired by hand in `UseCaseConfig` | Domain never depends on Spring or JDBC |
| Not applied | No interfaces for use cases or the calculator | One implementation each; would add indirection only |

## Quality

| | |
|---|---|
| Tests | 1,390 unit (1,000 of them random-balance precision cases) · 35 integration (Testcontainers PostgreSQL 17) |
| Coverage (domain + application, gated at 90%) | lines 72/73 · branches 10/10 |
| Mutation (PIT, domain + application, gated at 90% in CI) | 33/33 killed · test strength 100% |
| History | feat, refactor (each names smell + move), test, ci, docs and chore commits; see `git log --oneline` |
| Time | estimate 9.75 h · actual 2.6 h elapsed, incl. ~30 min of setup waits ([docs/TICKET.md](docs/TICKET.md)) |

## Production next steps

- **Idempotent monthly run:** an "interest applied for month M" record per deposit, so a retry cannot pay twice (A-3).
- **Exact money everywhere:** move the legacy maths to `BigDecimal` behind a versioned, business-approved change; the precision test shows the impact.
- **Audit and events:** append-only interest postings and a `BalanceUpdated` event instead of overwriting balances.
- **Security:** OAuth2 resource server or mTLS at the gateway, operations role for the update (A-11); rate limiting.
- **Scale:** process the book in pages inside the lock; paginate the list.
- **API, platform and QA** (to discuss, not built: overengineering for this kata):
  - an `Idempotency-Key` header or per-deposit "paid for month M" marker on the update (A-3);
  - RFC 7807 error bodies, `/v1` versioning and paging once there are real consumers;
  - correlation-id logs and run metrics pushed with OpenTelemetry (Actuator would add endpoints);
  - a buildpacks container image with a Trivy scan, a CycloneDX SBOM, and migrations as their own deploy step;
  - a load test of the monthly run on 100k deposits to size the lock and paging;
  - branch protection on `main` (CI required, no direct pushes).
- **From the self-review** (open, medium or low):
  - a transaction timeout on the update (`spring.transaction.default-timeout`), so a stuck run cannot hold the book's locks;
  - a deposit whose interest would cross the 10^13 cap is flagged and skipped instead of failing everyone's run;
  - Swagger UI and `/v3/api-docs` only in the local and demo profiles;
  - the demo seed advances the id sequences after its explicit ids;
  - tests for drift at 9,999,999,999,999.99, an update on an empty book, the 500 error path, and `days`/`planType` left untouched;
  - `List.copyOf` in `TimeDepositOverview`, and a stale `NUMERIC(19, 2)` comment in `JdbcTimeDepositRepository`.
- **Observability:** metrics and structured logs for each run (deposits updated, total interest).

## AI-assisted development

Built with Claude Code driven by a committed rulebook and phased prompt. Tools, setup, how to reproduce, and what was AI-assisted: [docs/ai/README.md](docs/ai/README.md).
