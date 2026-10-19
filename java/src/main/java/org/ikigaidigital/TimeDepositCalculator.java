package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {
  public void updateBalance(List<TimeDeposit> deposits) {
    for (TimeDeposit deposit : deposits) {
      deposit.setBalance(deposit.getBalance() + roundToCents(monthlyInterest(deposit)));
    }
  }

  private double monthlyInterest(TimeDeposit deposit) {
    double interest = 0;

    if (deposit.getDays() > 30) {
      if (deposit.getPlanType().equals("student")) {
        if (deposit.getDays() < 366) {
          interest += deposit.getBalance() * 0.03 / 12;
        }
      } else if (deposit.getPlanType().equals("premium")) {
        if (deposit.getDays() > 45) {
          interest += deposit.getBalance() * 0.05 / 12;
        }
      } else if (deposit.getPlanType().equals("basic")) {
        interest += deposit.getBalance() * 0.01 / 12;
      }
    }

    return interest;
  }

  // new BigDecimal(double), not BigDecimal.valueOf: it rounds the exact binary value, and changing
  // it would change results (e.g. basic 18.00 earns 0.01, not 0.02). Behaviour must stay the same.
  private static double roundToCents(double amount) {
    return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
