package org.ikigaidigital.application;

import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.TimeDepositRepository;

/** Applies one month of interest to every stored deposit through the legacy calculator. */
public class UpdateAllBalances {

  private final TimeDepositRepository repository;
  private final TimeDepositCalculator calculator;

  public UpdateAllBalances(TimeDepositRepository repository, TimeDepositCalculator calculator) {
    this.repository = repository;
    this.calculator = calculator;
  }

  public void updateAll() {
    List<TimeDeposit> deposits = repository.findAllForUpdate();
    calculator.updateBalance(deposits);
    repository.saveBalances(deposits);
  }
}
