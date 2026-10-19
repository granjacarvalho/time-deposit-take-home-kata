package org.ikigaidigital.interest;

import java.util.List;

/**
 * The plans the calculator applies by default. A new plan is a new {@link InterestRule} added here;
 * {@code TimeDepositCalculator} stays closed for modification (OCP, AC-6).
 */
public final class InterestRules {

  private InterestRules() {}

  /** Basic, student and premium, as the brief defines them. */
  public static List<InterestRule> briefPlans() {
    return List.of(new BasicPlanInterest(), new StudentPlanInterest(), new PremiumPlanInterest());
  }
}
