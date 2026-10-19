package org.ikigaidigital.interest;

import org.ikigaidigital.TimeDeposit;

public final class StudentPlanInterest implements InterestRule {

  @Override
  public String planType() {
    return "student";
  }

  @Override
  public double monthlyInterest(TimeDeposit deposit) {
    if (deposit.getDays() > 30 && deposit.getDays() < 366) {
      return deposit.getBalance() * 0.03 / 12;
    }
    return 0;
  }
}
