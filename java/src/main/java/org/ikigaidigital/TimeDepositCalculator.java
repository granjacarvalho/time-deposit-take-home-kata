package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {
  public void updateBalance(List<TimeDeposit> deposits) {
    for (TimeDeposit deposit : deposits) {
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

      double newBalance =
          deposit.getBalance()
              + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
      deposit.setBalance(newBalance);
    }
  }
}
