package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ikigaidigital.interest.InterestRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AC-6 a new plan is added without editing the calculator")
class TimeDepositCalculatorExtensionTest {

  // A plan the brief does not have: a flat 10.00 a month from day one.
  private static final InterestRule CORPORATE_PLAN =
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

  @Test
  void should_apply_registered_rule_when_plan_is_new() {
    TimeDeposit deposit = new TimeDeposit(1, "corporate", 1200.00, 10);

    new TimeDepositCalculator(List.of(CORPORATE_PLAN)).updateBalance(List.of(deposit));

    assertThat(deposit.getBalance()).isEqualTo(1210.00);
  }
}
