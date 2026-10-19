package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@DisplayName("AC-1 updateBalance keeps the results of the untouched legacy code")
class TimeDepositCalculatorCharacterizationTest {

  // Exact double equality on purpose: behaviour must stay identical, float noise included.
  @ParameterizedTest(name = "{0} plan, {1} days, balance {2} -> {3}")
  @CsvFileSource(resources = "/characterization/update-balance.csv", numLinesToSkip = 1)
  void should_match_recorded_balance_when_updated(
      String planType, int days, double balance, double expectedBalance) {
    TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

    new TimeDepositCalculator().updateBalance(List.of(deposit));

    assertThat(deposit.getBalance()).isEqualTo(expectedBalance);
  }

  @Test
  void should_update_each_deposit_independently_when_list_has_several() {
    TimeDeposit premium = new TimeDeposit(1, "premium", 1000.00, 46);
    TimeDeposit unknownPlan = new TimeDeposit(2, "gold", 1000.00, 46);

    new TimeDepositCalculator().updateBalance(List.of(premium, unknownPlan));

    assertThat(premium.getBalance()).isEqualTo(1004.17);
    assertThat(unknownPlan.getBalance()).isEqualTo(1000.00);
  }
}
