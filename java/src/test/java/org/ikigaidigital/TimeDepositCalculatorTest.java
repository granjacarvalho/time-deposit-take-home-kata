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

  @Nested
  @DisplayName("AC-3 basic plan earns 1% a year, paid monthly")
  class BasicPlan {

    @ParameterizedTest(name = "basic plan at {0} days -> 1201.00")
    @CsvSource({"31", "365", "1000"})
    void should_add_a_month_of_1_percent_when_basic_is_older_than_30_days(int days) {
      assertThat(balanceAfterUpdate("basic", days)).isEqualTo(1201.00);
    }
  }

  @Nested
  @DisplayName("AC-4 student plan earns 3% a year, but nothing after the first year")
  class StudentPlan {

    @ParameterizedTest(name = "student plan at {0} days -> 1203.00")
    @CsvSource({"31", "365"})
    void should_add_a_month_of_3_percent_when_student_is_within_first_year(int days) {
      assertThat(balanceAfterUpdate("student", days)).isEqualTo(1203.00);
    }

    // A-4: "after 1 year" follows the legacy code, days < 366, so day 365 still earns.
    @ParameterizedTest(name = "student plan at {0} days keeps 1200.00")
    @CsvSource({"366", "1000"})
    void should_keep_balance_when_student_is_older_than_a_year(int days) {
      assertThat(balanceAfterUpdate("student", days)).isEqualTo(BALANCE);
    }
  }

  @Nested
  @DisplayName("AC-5 premium plan earns 5% a year, starting after 45 days")
  class PremiumPlan {

    @ParameterizedTest(name = "premium plan at {0} days -> 1205.00")
    @CsvSource({"46", "365"})
    void should_add_a_month_of_5_percent_when_premium_is_older_than_45_days(int days) {
      assertThat(balanceAfterUpdate("premium", days)).isEqualTo(1205.00);
    }

    @ParameterizedTest(name = "premium plan at {0} days keeps 1200.00")
    @CsvSource({"31", "45"})
    void should_keep_balance_when_premium_is_45_days_or_younger(int days) {
      assertThat(balanceAfterUpdate("premium", days)).isEqualTo(BALANCE);
    }
  }
}
