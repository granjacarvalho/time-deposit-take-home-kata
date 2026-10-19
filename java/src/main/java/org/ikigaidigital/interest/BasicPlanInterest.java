package org.ikigaidigital.interest;

public final class BasicPlanInterest extends ExistingPlanInterest {

  @Override
  public String planType() {
    return "basic";
  }

  @Override
  protected double annualRate() {
    return 0.01;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return true;
  }
}
