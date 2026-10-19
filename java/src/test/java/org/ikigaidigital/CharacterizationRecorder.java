package org.ikigaidigital;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Records the golden master for {@code TimeDepositCalculatorCharacterizationTest}. Not a test: run
 * it once against the untouched legacy calculator (see the CSV header for the commit). Re-running
 * it after a refactor would only record the new behaviour, so never regenerate to make a test pass.
 */
public final class CharacterizationRecorder {

  // Plans the brief names, an unknown plan, and a differently cased one (A-4).
  private static final List<String> PLANS = List.of("basic", "student", "premium", "gold", "Basic");

  // Every boundary in the legacy rules: 30/31, 45/46, 365/366.
  private static final List<Integer> DAYS = List.of(0, 1, 30, 31, 45, 46, 365, 366, 1000);

  // Includes balances where new BigDecimal(double) rounds differently from exact decimal maths.
  private static final List<Double> BALANCES =
      List.of(0.01, 6.00, 18.00, 25.20, 30.00, 1000.00, 1234567.00, 99999999.99);

  private CharacterizationRecorder() {}

  public static void main(String[] args) throws IOException {
    StringBuilder csv = new StringBuilder();
    csv.append("# Golden master recorded from the untouched legacy TimeDepositCalculator (")
        .append(args[1])
        .append("). Do not edit by hand.\n");
    csv.append("planType,days,balance,expectedBalance\n");
    for (String plan : PLANS) {
      for (int days : DAYS) {
        for (double balance : BALANCES) {
          TimeDeposit deposit = new TimeDeposit(1, plan, balance, days);
          new TimeDepositCalculator().updateBalance(List.of(deposit));
          csv.append(plan)
              .append(',')
              .append(days)
              .append(',')
              .append(balance)
              .append(',')
              .append(deposit.getBalance())
              .append('\n');
        }
      }
    }
    Files.writeString(Path.of(args[0]), csv, StandardCharsets.UTF_8);
  }
}
