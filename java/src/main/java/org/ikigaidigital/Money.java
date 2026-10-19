package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Converts the legacy {@code double} balance to money with two decimals (A-5). */
public final class Money {

  private static final int CENTS_SCALE = 2;

  private Money() {}

  // valueOf uses the shortest decimal that round-trips, so 1235595.81 stays 1235595.81;
  // exact for balances below 10^13.
  public static BigDecimal fromLegacy(double amount) {
    return BigDecimal.valueOf(amount).setScale(CENTS_SCALE, RoundingMode.HALF_UP);
  }
}
