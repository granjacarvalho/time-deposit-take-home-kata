package org.ikigaidigital.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UpdateAllBalances saves what the calculator computed")
class UpdateAllBalancesTest {

  @Test
  void savesTheNewBalanceOfEveryDeposit() {
    var repository =
        new InMemoryTimeDepositRepository(
            new TimeDeposit(1, "basic", 1200.00, 31), new TimeDeposit(2, "gold", 1200.00, 31));

    new UpdateAllBalances(repository, new TimeDepositCalculator()).updateAll();

    assertThat(repository.savedBalances()).containsExactly(1201.00, 1200.00);
  }
}
