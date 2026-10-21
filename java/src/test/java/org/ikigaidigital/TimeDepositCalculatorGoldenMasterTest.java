package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Regression guard, not the spec: the rules are explained in {@link TimeDepositCalculatorTest}. The
 * CSV holds 360 results recorded from the untouched upstream calculator (commit c4ea358, see {@code
 * tools/CharacterizationRecorder.java}); it fails if any refactor changes a single cent.
 */
@DisplayName("Golden master: every result recorded from the untouched calculator still holds")
class TimeDepositCalculatorGoldenMasterTest {

  // Exact double equality on purpose: the brief forbids any change, float noise included.
  @ParameterizedTest(name = "{0} plan, {1} days, {2} becomes {3}")
  @CsvFileSource(resources = "/characterization/update-balance.csv", numLinesToSkip = 1)
  void keepsTheRecordedResult(String planType, int days, double balance, double expected) {
    TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);

    new TimeDepositCalculator().updateBalance(List.of(deposit));

    assertThat(deposit.getBalance()).isEqualTo(expected);
  }
}
