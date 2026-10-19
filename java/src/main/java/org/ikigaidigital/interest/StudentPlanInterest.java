package org.ikigaidigital.interest;

public final class StudentPlanInterest extends ExistingPlanInterest {

  @Override
  public String planType() {
    return "student";
  }

  @Override
  protected double annualRate() {
    return 0.03;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return days < 366;
  }
}
