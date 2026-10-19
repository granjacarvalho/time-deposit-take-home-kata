# IKG-1 · Time deposit API on the legacy interest calculator

**Story.** As XA Bank operations, I want an API that applies monthly interest to every stored time deposit and lists them with their withdrawals, so that balances stay correct and visible without breaking the existing calculator.

## Acceptance criteria

| AC | Brief | Given / When / Then |
|---|---|---|
| AC-1 | "Ensure its behavior remains unchanged" | Any deposit → `updateBalance` after the refactor → the balance recorded from the untouched code |
| AC-2 | "No interest … for the first 30 days" | Any plan at ≤ 30 days → update → balance unchanged |
| AC-3 | "Basic Plan: 1% interest" | Basic, > 30 days → update → `+ round½↑(balance × 0.01 / 12)` |
| AC-4 | "Student Plan: 3% interest (no interest after 1 year)" | Days 31–365 → `+ 3% / 12`; ≥ 366 → unchanged |
| AC-5 | "Premium Plan: 5% interest (interest starts after 45 days)" | ≥ 46 days → `+ 5% / 12`; 31–45 → unchanged |
| AC-6 | "extensible to accommodate future complexities" | A new plan's rule is registered → update → it applies, calculator unedited |
| AC-7 | "Store all time deposit plans in a database … tables" | Migrations ran → both tables have the brief's columns, keys and NOT NULLs, and reject invalid rows |
| AC-8 | "endpoint to update the balances of all time deposits" | `POST /time-deposits/balance-updates` → 204; every stored balance as AC-1–5; concurrent calls each apply one month |
| AC-9 | "retrieve all time deposits … withdrawals" | `GET /time-deposits` → 200 with every deposit and its withdrawals |

**Out of scope:** input validation and error handling (brief), authentication (A-11), pagination, creating deposits or withdrawals, advancing `days`, idempotent updates, the other language folders.

## Assumptions

| Id | Assumption | Why |
|---|---|---|
| A-1 | The update does not advance `days`; one call applies one month at the stored age | The legacy code never changes `days` |
| A-2 | Withdrawals are read-only: the balance is already net of them and they do not change interest; amounts are positive | The brief lists them only in the GET schema; no endpoint creates them |
| A-3 | The update is `POST`, 204, not idempotent: each call applies one more month | Applying interest twice changes the result, so `PUT` would be wrong |
| A-4 | Unknown or differently cased plan types earn no interest; "1 year" is `days < 366` | Legacy behaviour, pinned by AC-1 |
| A-5 | Money is `NUMERIC(15, 2)` / `BigDecimal` in the DB and API; the legacy `double` is converted at the edges with `BigDecimal.valueOf(d).setScale(2, HALF_UP)` | Below 10^13 a `double` holds every cent exactly; the schema rejects larger balances |
| A-6 | Table and column names are the brief's camelCase, unquoted (PostgreSQL stores them in lower case) | Matches the brief without quoting every identifier |
| A-7 | Deposits ordered by id; withdrawals by date, then id | Stable, readable output |
| A-8 | Demo data only for the local run (`spring-boot:test-run`, profile `demo`) | Production migrations hold no sample rows |
| A-9 | Swagger UI and `/v3/api-docs` are documentation, not API endpoints | The brief allows exactly two endpoints |
| A-10 | The solution lives in `java/`; the other language folders are untouched | The brief offers five languages; one is enough |
| A-11 | No authentication in the app: in production both endpoints sit behind the bank's gateway, and the update needs an operations role | Not in the brief; keeps the Swagger demo one click |
| A-12 | "1% interest" is a nominal annual rate paid monthly (rate / 12), rounded HALF_UP to cents each month | The legacy code divides by 12 |

## Estimate vs actual

Estimate 9.75 h against a 10 h time-box. Actual: see the README quality numbers (work time from the time log; Docker installation waits excluded).
