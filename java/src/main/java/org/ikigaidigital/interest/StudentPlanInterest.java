package org.ikigaidigital.interest;

public final class StudentPlanInterest extends ExistingPlanInterest {

  private static final double ANNUAL_RATE = 0.03;

  // A-4: "no interest after 1 year" follows the legacy days < 366, so day 365 still earns.
  private static final int FIRST_DAY_AFTER_ONE_YEAR = 366;

  @Override
  public String planType() {
    return "student";
  }

  @Override
  protected double annualRate() {
    return ANNUAL_RATE;
  }

  @Override
  protected boolean earnsInterestAt(int days) {
    return days < FIRST_DAY_AFTER_ONE_YEAR;
  }
}
