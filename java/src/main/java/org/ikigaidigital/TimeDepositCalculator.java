package org.ikigaidigital;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.ikigaidigital.interest.BasicPlanInterest;
import org.ikigaidigital.interest.InterestRule;
import org.ikigaidigital.interest.PremiumPlanInterest;
import org.ikigaidigital.interest.StudentPlanInterest;

public class TimeDepositCalculator {

  private final Map<String, InterestRule> rulesByPlanType =
      List.of(new BasicPlanInterest(), new StudentPlanInterest(), new PremiumPlanInterest())
          .stream()
          .collect(toUnmodifiableMap(InterestRule::planType, identity()));

  public void updateBalance(List<TimeDeposit> deposits) {
    for (TimeDeposit deposit : deposits) {
      deposit.setBalance(deposit.getBalance() + roundToCents(monthlyInterest(deposit)));
    }
  }

  // A-4: an unknown or differently cased plan type earns no interest, as in the legacy code.
  private double monthlyInterest(TimeDeposit deposit) {
    InterestRule rule = rulesByPlanType.get(deposit.getPlanType());
    return rule == null ? 0 : rule.monthlyInterest(deposit);
  }

  // new BigDecimal(double), not BigDecimal.valueOf: it rounds the exact binary value, and changing
  // it would change results (e.g. basic 18.00 earns 0.01, not 0.02). Behaviour must stay the same.
  private static double roundToCents(double amount) {
    return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
