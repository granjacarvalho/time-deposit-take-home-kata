package org.ikigaidigital.interest;

public final class BasicPlanInterest extends ExistingPlanInterest {

  private static final double ANNUAL_RATE = 0.01;

  @Override
  public String planType() {
    return "basic";
  }

  @Override
  protected double annualRate() {
    return ANNUAL_RATE;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return true;
  }
}
