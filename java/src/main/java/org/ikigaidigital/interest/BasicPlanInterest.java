package org.ikigaidigital.interest;

import org.ikigaidigital.TimeDeposit;

public final class BasicPlanInterest implements InterestRule {

  @Override
  public String planType() {
    return "basic";
  }

  @Override
  public double monthlyInterest(TimeDeposit deposit) {
    if (deposit.getDays() > 30) {
      return deposit.getBalance() * 0.01 / 12;
    }
    return 0;
  }
}
