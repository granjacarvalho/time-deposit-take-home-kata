package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The one-cent bound of {@code InterestPrecisionTest}, checked on random balances in CI instead of
 * only in a scratch probe. The seed is fixed, so every run checks the same cases.
 */
@DisplayName("AC-1 legacy interest stays within one cent of exact interest, for random balances")
class InterestPrecisionPropertyTest {

  private static final long SEED = 20261019L;
  private static final int CASES = 1_000;
  private static final int MAX_DIGITS_OF_CENTS = 15; // balances below 10^13, the schema's cap
  private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
  private static final BigDecimal ONE_CENT = new BigDecimal("0.01");

  // plan type, annual rate, first and last day this test draws from (all days that earn interest)
  private static final Object[][] PLANS = {
    {"basic", "0.01", 31, 2000}, {"student", "0.03", 31, 365}, {"premium", "0.05", 46, 2000}
  };

  static Stream<Arguments> randomEligibleDeposits() {
    Random random = new Random(SEED);
    return Stream.generate(() -> randomDeposit(random)).limit(CASES);
  }

  private static Arguments randomDeposit(Random random) {
    Object[] plan = PLANS[random.nextInt(PLANS.length)];
    int firstDay = (int) plan[2];
    int days = firstDay + random.nextInt((int) plan[3] - firstDay + 1);
    // Mix magnitudes: pick how many digits the cents have, then a value below that.
    long bound = (long) Math.pow(10, 1 + random.nextInt(MAX_DIGITS_OF_CENTS));
    BigDecimal balance = BigDecimal.valueOf(Math.floorMod(random.nextLong(), bound), 2);
    return Arguments.of(plan[0], days, balance, new BigDecimal((String) plan[1]));
  }

  @ParameterizedTest(name = "{0} plan, {1} days, balance {2}")
  @MethodSource("randomEligibleDeposits")
  void should_stay_within_one_cent_of_exact_interest_when_balance_is_random(
      String planType, int days, BigDecimal balance, BigDecimal annualRate) {
    TimeDeposit deposit = new TimeDeposit(1, planType, balance.doubleValue(), days);
    BigDecimal exact =
        balance.add(balance.multiply(annualRate).divide(MONTHS_PER_YEAR, 2, RoundingMode.HALF_UP));

    new TimeDepositCalculator().updateBalance(List.of(deposit));

    assertThat(Money.fromLegacy(deposit.getBalance()))
        .isGreaterThanOrEqualTo(balance)
        .isCloseTo(exact, within(ONE_CENT));
  }
}
