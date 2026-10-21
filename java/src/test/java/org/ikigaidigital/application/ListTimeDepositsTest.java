package org.ikigaidigital.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.Withdrawal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ListTimeDeposits pairs each deposit with its withdrawals")
class ListTimeDepositsTest {

  private static final Withdrawal WITHDRAWAL =
      new Withdrawal(10, 2, new BigDecimal("100.25"), LocalDate.parse("2026-09-15"));

  @Test
  void attachesEachWithdrawalToItsDeposit() {
    var deposits =
        new InMemoryTimeDepositRepository(
            new TimeDeposit(1, "basic", 1235595.81, 45), new TimeDeposit(2, "premium", 18.0, 46));

    var overviews = new ListTimeDeposits(deposits, () -> List.of(WITHDRAWAL)).listAll();

    assertThat(overviews)
        .containsExactly(
            new TimeDepositOverview(1, "basic", new BigDecimal("1235595.81"), 45, List.of()),
            new TimeDepositOverview(
                2, "premium", new BigDecimal("18.00"), 46, List.of(WITHDRAWAL)));
  }
}
