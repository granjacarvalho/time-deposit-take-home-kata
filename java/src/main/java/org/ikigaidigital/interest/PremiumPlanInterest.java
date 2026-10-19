package org.ikigaidigital.interest;

public final class PremiumPlanInterest extends ExistingPlanInterest {

  private static final double ANNUAL_RATE = 0.05;
  private static final int LAST_DAY_WITHOUT_INTEREST = 45;

  @Override
  public String planType() {
    return "premium";
  }

  @Override
  protected double annualRate() {
    return ANNUAL_RATE;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return days > LAST_DAY_WITHOUT_INTEREST;
  }
}
