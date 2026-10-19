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

  private static final int CENTS_SCALE = 2;

  // A-4: an unknown or differently cased plan type earns no interest, as in the legacy code.
  private static final InterestRule NO_INTEREST =
      new InterestRule() {
        @Override
        public String planType() {
          return "";
        }

        @Override
        public double monthlyInterest(TimeDeposit deposit) {
          return 0;
        }
      };

  private final Map<String, InterestRule> rulesByPlanType;

  /** The three plans the brief defines; existing callers keep using this constructor. */
  public TimeDepositCalculator() {
    this(List.of(new BasicPlanInterest(), new StudentPlanInterest(), new PremiumPlanInterest()));
  }

  /** Extension point (AC-6): a new plan is a new {@link InterestRule}, registered here. */
  public TimeDepositCalculator(List<InterestRule> rules) {
    this.rulesByPlanType =
        rules.stream().collect(toUnmodifiableMap(InterestRule::planType, identity()));
  }

  public void updateBalance(List<TimeDeposit> deposits) {
    for (TimeDeposit deposit : deposits) {
      deposit.setBalance(deposit.getBalance() + roundToCents(monthlyInterest(deposit)));
    }
  }

  private double monthlyInterest(TimeDeposit deposit) {
    return rulesByPlanType
        .getOrDefault(deposit.getPlanType(), NO_INTEREST)
        .monthlyInterest(deposit);
  }

  // new BigDecimal(double), not BigDecimal.valueOf: it rounds the exact binary value, and changing
  // it would change results (e.g. basic 18.00 earns 0.01, not 0.02). Behaviour must stay the same.
  private static double roundToCents(double amount) {
    return new BigDecimal(amount).setScale(CENTS_SCALE, RoundingMode.HALF_UP).doubleValue();
  }
}
