# Evidence

Captured on 2026-10-19 with `npx playwright@1.56.1 screenshot` (and a short Playwright script for Swagger) from the local run on commit `1ee9caa`. Terminal output is shown in `<pre>` pages, excerpted to the summary lines.

| File | Shows |
|---|---|
| [verify.png](verify.png) | `./mvnw -B verify`: 390 unit + 34 integration tests, Spotless clean, coverage gate met, BUILD SUCCESS |
| [coverage-jacoco.png](coverage-jacoco.png) | JaCoCo report merged from unit and integration tests |
| [mutation-pit-report.png](mutation-pit-report.png) · [mutation-pit-summary.png](mutation-pit-summary.png) | PIT: 32/32 mutants killed, test strength 100% |
| [swagger-ui.png](swagger-ui.png) | Swagger UI serving the hand-written contract: exactly two operations |
| [swagger-get-time-deposits.png](swagger-get-time-deposits.png) | GET executed from Swagger: 200, demo data, `cache-control: no-store`, `x-content-type-options: nosniff` |
| [git-log.png](git-log.png) | `git log --oneline --graph`: characterization before refactors, one AC per `feat`, one smell per `refactor` |
| [ci-run.png](ci-run.png) | GitHub Actions run (commit and run id in the image) |
