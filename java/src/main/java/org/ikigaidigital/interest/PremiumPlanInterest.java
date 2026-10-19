package org.ikigaidigital.interest;

import org.ikigaidigital.TimeDeposit;

public final class PremiumPlanInterest implements InterestRule {

  @Override
  public String planType() {
    return "premium";
  }

  @Override
  public double monthlyInterest(TimeDeposit deposit) {
    if (deposit.getDays() > 30 && deposit.getDays() > 45) {
      return deposit.getBalance() * 0.05 / 12;
    }
    return 0;
  }
}
