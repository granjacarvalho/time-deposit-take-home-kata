package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The legacy maths must stay (AC-1), so it cannot be exact; this pins how far it may drift. Formula
 * from the brief: one month = balance x annual rate / 12, rounded HALF_UP to cents. A scratch probe
 * over 30.6M balances (0.01 to 100,000.00 and two bands near 10^9 and 10^13) found the stored
 * result never more than 0.01 from exact decimal maths. exactBalance below is that formula in
 * BigDecimal, e.g. 18.00 x 0.01 / 12 = 0.015 -> 0.02, where the legacy code gives 0.01.
 */
@DisplayName("AC-1 legacy interest stays within one cent of exact decimal interest")
class InterestPrecisionTest {

  private static final BigDecimal ONE_CENT = new BigDecimal("0.01");

  @ParameterizedTest(name = "{0} plan, balance {2}: exact {3}, legacy within one cent")
  @CsvSource(
      useHeadersInDisplayName = true,
      textBlock =
          """
          planType, days, balance,          exactBalance
          basic,    31,   18.00,            18.02
          basic,    31,   30.00,            30.03
          student,  31,   6.00,             6.02
          student,  31,   10.00,            10.03
          premium,  46,   18.00,            18.08
          premium,  46,   25.20,            25.31
          basic,    31,   1234567.00,       1235595.81
          premium,  46,   9999999999999.99, 10041666666666.66
          """)
  void should_stay_within_one_cent_of_exact_interest_when_updated(
      String planType, int days, double balance, BigDecimal exactBalance) {
    TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

    new TimeDepositCalculator().updateBalance(List.of(deposit));

    BigDecimal stored = Money.fromLegacy(deposit.getBalance());
    assertThat(stored).isCloseTo(exactBalance, within(ONE_CENT));
  }
}
