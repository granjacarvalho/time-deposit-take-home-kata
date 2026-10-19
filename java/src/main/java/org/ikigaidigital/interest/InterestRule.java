package org.ikigaidigital.interest;

import org.ikigaidigital.TimeDeposit;

/** One plan's monthly interest. A new plan is a new implementation; the calculator is untouched. */
public interface InterestRule {

  /** The {@code planType} value this rule applies to, matched exactly (A-4). */
  String planType();

  /** Unrounded monthly interest, in the legacy {@code double} arithmetic. */
  double monthlyInterest(TimeDeposit deposit);
}
