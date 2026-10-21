# Traceability · AC → test → evidence

All tests pass in `./mvnw verify`: 389 unit tests (360 of them the golden master) and 24 integration tests; PIT kills 33/33 mutants and gates at 90% in CI. Red reasons are what a test showed before the code existed; AC-1 to AC-5 describe existing behaviour, so their sensitivity is shown instead.

| AC | Test class · nested group | What it shows | Red reason or sensitivity |
|---|---|---|---|
| AC-1 | `TimeDepositCalculatorGoldenMasterTest` | 360 results recorded from upstream `c4ea358` still hold (regression guard, not the spec) | Changing `> 30` to `>= 30` fails 14 cases |
| AC-1 | `TimeDepositCalculatorTest` · *when rounding to cents* | 6 legacy results at rounding traps, e.g. basic 18.00 → 18.01 | n/a, existing behaviour |
| AC-1 | `TimeDepositCalculatorTest` · *for several deposits at once* | Each deposit is updated on its own terms | n/a, existing behaviour |
| AC-2 | `TimeDepositCalculatorTest` · *in the first 30 days* | No plan pays interest up to day 30 | n/a, existing behaviour |
| AC-3 | `TimeDepositCalculatorTest` · *on the basic plan* | 1200.00 becomes 1201.00 from day 31 | n/a, existing behaviour |
| AC-4 | `TimeDepositCalculatorTest` · *on the student plan* | 1203.00 up to day 365, nothing from day 366 | n/a, existing behaviour |
| AC-5 | `TimeDepositCalculatorTest` · *on the premium plan* | Nothing up to day 45, 1205.00 from day 46 | n/a, existing behaviour |
| A-4 | `TimeDepositCalculatorTest` · *for a plan it does not know* | `gold` and `Basic` earn nothing | n/a, existing behaviour |
| AC-6 | `TimeDepositCalculatorTest` · *with a new plan registered* | A registered rule applies without changing the calculator | expected 1210.0 but was 1200.0: stub constructor ignored the rule |
| AC-7 | `DatabaseConstraintsIT` | Missing plan type, negative balance or days, balances of 10^13+, unknown deposit and non-positive withdrawals are rejected; both balance limits are accepted | 11/11 failed before V1, 5/5 bad rows accepted before V2 |
| AC-8 | `TimeDepositsApiIT` · *POST /time-deposits/balance-updates* | One month for every deposit; four parallel calls pay four months | balances unchanged (stub use case); 1202.00 instead of 1204.00 without the row lock |
| AC-8 | `JdbcTimeDepositRepositoryIT` | Balances keep every cent between NUMERIC and the legacy double, up to 9,999,999,999,999.99 | 8/8 failed against the stub repository |
| AC-8 | `UpdateAllBalancesTest` | The use case saves what the calculator computed | added for PIT no-coverage mutants |
| AC-9 | `TimeDepositsApiIT` · *GET /time-deposits* | Every deposit with its withdrawals, in order; an empty list | expected 200 OK but was 501 |
| AC-9 | `ListTimeDepositsTest` | Each withdrawal is attached to its deposit | added for PIT no-coverage mutants |
| Security | `TimeDepositsApiIT` · *every response* | `no-store` and `nosniff` on every response; errors hide internals | Cache-Control was null on GET, POST and a 405 |
| Design | `ArchitectureTest` | Domain and application use no Spring and no adapters | fails when a use case gets `@Component` |
