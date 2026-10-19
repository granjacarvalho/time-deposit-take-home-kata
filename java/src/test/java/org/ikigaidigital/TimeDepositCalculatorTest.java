package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// The interest rules as a readable spec. 1200.00 makes each month's interest a round number:
// basic 1.00, student 3.00, premium 5.00.
class TimeDepositCalculatorTest {

  private static final double BALANCE = 1200.00;

  private static double balanceAfterUpdate(String planType, int days) {
    TimeDeposit deposit = new TimeDeposit(1, planType, BALANCE, days);
    new TimeDepositCalculator().updateBalance(List.of(deposit));
    return deposit.getBalance();
  }

  @Nested
  @DisplayName("AC-2 no interest in the first 30 days")
  class FirstThirtyDays {

    @ParameterizedTest(name = "{0} plan at {1} days keeps 1200.00")
    @CsvSource({"basic, 0", "basic, 30", "student, 1", "student, 30", "premium, 30"})
    void should_keep_balance_when_deposit_is_30_days_or_younger(String planType, int days) {
      assertThat(balanceAfterUpdate(planType, days)).isEqualTo(BALANCE);
    }
  }
}
