package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ikigaidigital.interest.InterestRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("TimeDepositCalculator applies one month of interest")
class TimeDepositCalculatorTest {

  private final TimeDepositCalculator calculator = new TimeDepositCalculator();

  @Nested
  @DisplayName("in the first 30 days")
  class FirstThirtyDays {

    @ParameterizedTest(name = "{0} plan at {1} days keeps 1200.00")
    @CsvSource({"basic, 0", "basic, 30", "student, 30", "premium, 30"})
    void paysNoInterest(String planType, int days) {
      TimeDeposit deposit = new TimeDeposit(1, planType, 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1200.00);
    }
  }

  @Nested
  @DisplayName("on the basic plan")
  class BasicPlan {

    @ParameterizedTest(name = "at {0} days, 1200.00 becomes 1201.00")
    @ValueSource(ints = {31, 365, 1000})
    void paysOneTwelfthOfOnePercent(int days) {
      TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1201.00);
    }
  }

  @Nested
  @DisplayName("on the student plan")
  class StudentPlan {

    @ParameterizedTest(name = "at {0} days, 1200.00 becomes 1203.00")
    @ValueSource(ints = {31, 365})
    void paysOneTwelfthOfThreePercentInTheFirstYear(int days) {
      TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1203.00);
    }

    // A-4: "no interest after 1 year" follows the legacy days < 366, so day 365 still earns.
    @ParameterizedTest(name = "at {0} days, 1200.00 stays 1200.00")
    @ValueSource(ints = {366, 1000})
    void paysNothingAfterTheFirstYear(int days) {
      TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1200.00);
    }
  }

  @Nested
  @DisplayName("on the premium plan")
  class PremiumPlan {

    @ParameterizedTest(name = "at {0} days, 1200.00 becomes 1205.00")
    @ValueSource(ints = {46, 365})
    void paysOneTwelfthOfFivePercentAfter45Days(int days) {
      TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1205.00);
    }

    @ParameterizedTest(name = "at {0} days, 1200.00 stays 1200.00")
    @ValueSource(ints = {31, 45})
    void paysNothingUpTo45Days(int days) {
      TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1200.00);
    }
  }

  @Nested
  @DisplayName("for a plan it does not know")
  class UnknownPlan {

    // A-4: plan types match exactly, so a differently cased name is unknown too.
    @ParameterizedTest(name = "\"{0}\" keeps 1200.00")
    @ValueSource(strings = {"gold", "Basic"})
    void paysNoInterest(String planType) {
      TimeDeposit deposit = new TimeDeposit(1, planType, 1200.00, 100);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1200.00);
    }
  }

  @Nested
  @DisplayName("when rounding to cents, like the legacy code")
  class Rounding {

    // The legacy code rounds the binary double, so a half cent can go down: basic 18.00 earns
    // 0.01 where exact maths would give 0.02. The brief forbids changing that (A-5).
    @ParameterizedTest(name = "{0} plan at {1} days: {2} becomes {3}")
    @CsvSource({
      "basic,   31, 18.00,      18.01",
      "basic,   31, 30.00,      30.02",
      "student, 31, 6.00,       6.01",
      "premium, 46, 18.00,      18.07",
      "premium, 46, 25.20,      25.30",
      "basic,   45, 1234567.00, 1235595.81"
    })
    void keepsTheLegacyResult(String planType, int days, double balance, double expected) {
      TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

      calculator.updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(expected);
    }
  }

  @Nested
  @DisplayName("for several deposits at once")
  class SeveralDeposits {

    @Test
    void updatesEachDepositOnItsOwnTerms() {
      TimeDeposit premium = new TimeDeposit(1, "premium", 1000.00, 46);
      TimeDeposit unknownPlan = new TimeDeposit(2, "gold", 1000.00, 46);

      calculator.updateBalance(List.of(premium, unknownPlan));

      assertThat(premium.getBalance()).isEqualTo(1004.17);
      assertThat(unknownPlan.getBalance()).isEqualTo(1000.00);
    }
  }

  @Nested
  @DisplayName("with a new plan registered")
  class NewPlan {

    @Test
    void appliesTheNewPlanWithoutChangingTheCalculator() {
      InterestRule corporate =
          new InterestRule() {
            @Override
            public String planType() {
              return "corporate";
            }

            @Override
            public double monthlyInterest(TimeDeposit deposit) {
              return 10.00;
            }
          };
      TimeDeposit deposit = new TimeDeposit(1, "corporate", 1200.00, 10);

      new TimeDepositCalculator(List.of(corporate)).updateBalance(List.of(deposit));

      assertThat(deposit.getBalance()).isEqualTo(1210.00);
    }
  }
}
