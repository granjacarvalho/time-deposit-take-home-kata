# Traceability · AC → test → evidence

All tests pass in `./mvnw verify`: 1,390 unit and 35 integration tests; PIT gates mutation coverage at 90% in CI. Red reasons are what the test showed before the code existed; for existing behaviour (AC-1–5) there is no red by design, so sensitivity is shown instead.

| AC | Test (class · method) | Cases | Red reason | Status |
|---|---|---|---|---|
| AC-1 | `TimeDepositCalculatorCharacterizationTest` · `should_match_recorded_balance_when_updated` | 360: 5 plan types × 9 day boundaries × 8 balances, recorded from upstream `c4ea358` | n/a, characterization. Changing `> 30` to `>= 30` fails 14 cases | ✅ |
| AC-1 | … · `should_update_each_deposit_independently_when_list_has_several` | 1 | n/a, characterization | ✅ |
| AC-1 | `InterestPrecisionTest` · `should_stay_within_one_cent_of_exact_interest_when_updated` | 8: rounding traps, 10^13 balance | n/a, measures existing maths | ✅ |
| AC-1 | `InterestPrecisionPropertyTest` · `should_stay_within_one_cent_of_exact_interest_when_balance_is_random` | 1,000 seeded random balances, all magnitudes below 10^13 | n/a, measures existing maths. Dividing by 11 instead of 12 fails 757 cases | ✅ |
| AC-2 | `TimeDepositCalculatorTest` · `should_keep_balance_when_deposit_is_30_days_or_younger` | 5 | n/a, existing behaviour | ✅ |
| AC-3 | … · `should_add_a_month_of_1_percent_when_basic_is_older_than_30_days` | 3 | n/a, existing behaviour | ✅ |
| AC-4 | … · `should_add_a_month_of_3_percent_when_student_is_within_first_year` · `should_keep_balance_when_student_is_older_than_a_year` | 2 + 2 | n/a, existing behaviour | ✅ |
| AC-5 | … · `should_add_a_month_of_5_percent_when_premium_is_older_than_45_days` · `should_keep_balance_when_premium_is_45_days_or_younger` | 2 + 2 | n/a, existing behaviour | ✅ |
| AC-6 | `TimeDepositCalculatorExtensionTest` · `should_apply_registered_rule_when_plan_is_new` | 1 | expected 1210.0 but was 1200.0: stub constructor ignored the rule | ✅ |
| AC-7 | `SchemaIT` · `should_define_column_as_specified` · `should_use_id_as_primary_key` · `should_reject_withdrawal_when_time_deposit_does_not_exist` | 8 + 2 + 1 | 11/11 failed: no tables yet | ✅ |
| AC-7 | `SchemaIT` · `should_reject_time_deposit_when_it_breaks_a_rule` · `should_reject_withdrawal_when_amount_is_not_positive` · `should_accept_time_deposit_when_balance_is_at_a_limit` | 3 + 2 + 2 | 5/5 bad rows accepted by V1 | ✅ |
| AC-8 | `UpdateBalancesIT` · `should_apply_monthly_interest_to_every_deposit_when_balances_are_updated` | 5 deposits: each plan, unknown plan, rounding trap | balances unchanged (stub use case) | ✅ |
| AC-8 | `UpdateBalancesIT` · `should_apply_one_month_per_call_when_updates_run_concurrently` | 4 parallel calls | 1202.00, expected 1204.00: lost updates | ✅ |
| AC-8 | `JdbcTimeDepositRepositoryIT` · `should_load_balance_as_legacy_double_when_found` · `should_store_balance_without_drift_when_saved` | 4 + 4 | 8/8 failed: stub repository | ✅ |
| AC-8 | `UpdateAllBalancesTest` · `should_save_monthly_interest_for_every_deposit_when_updated` | 1 | added for PIT no-coverage mutants | ✅ |
| AC-9 | `ListTimeDepositsIT` · `should_return_every_deposit_with_its_withdrawals_when_listed` · `should_return_empty_list_when_no_deposits_are_stored` | 1 + 1 | expected 200 OK but was 501 | ✅ |
| AC-9 | `ListTimeDepositsTest` · `should_attach_withdrawals_to_their_deposit_when_listed` | 1 | added for PIT no-coverage mutants | ✅ |
| AC-8/9 | `ApiSurfaceIT` · `should_expose_exactly_the_contract_operations_when_running` | 1: request mappings vs `openapi.yaml` | n/a, guard. A temporary `GET /extra` fails it | ✅ |
| Security | `ApiHardeningIT` · `should_forbid_caching_and_sniffing_when_endpoint_answers` · `should_hide_internals_when_request_fails` | 2 + 1 | Cache-Control was null on GET, POST and a 405 | ✅ |
| Design | `ArchitectureTest` · `should_keep_domain_and_application_free_of_spring` · `…independent_of_adapters` | 2 | rule fails when a use case gets `@Component` | ✅ |
| Smoke | `TimeDepositApplicationIT` · `should_start_when_database_is_available` | 1 | n/a, scaffold | ✅ |
