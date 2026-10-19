package org.ikigaidigital.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Shared shape of the three existing plans: no interest in the first 30 days (the brief says "for
 * any existing plans"), then one twelfth of an annual rate. A future plan with a different shape
 * implements {@link InterestRule} directly.
 */
abstract class ExistingPlanInterest implements InterestRule {

  @Override
  public final double monthlyInterest(TimeDeposit deposit) {
    if (deposit.getDays() > 30 && earnsInterestAt(deposit.getDays())) {
      return deposit.getBalance() * annualRate() / 12;
    }
    return 0;
  }

  protected abstract double annualRate();

  /** Plan-specific limits on top of the first 30 days. */
  protected abstract boolean earnsInterestAt(int days);
}
