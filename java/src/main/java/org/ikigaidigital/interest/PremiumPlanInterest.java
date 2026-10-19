package org.ikigaidigital.interest;

public final class PremiumPlanInterest extends ExistingPlanInterest {

  @Override
  public String planType() {
    return "premium";
  }

  @Override
  protected double annualRate() {
    return 0.05;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return days > 45;
  }
}
