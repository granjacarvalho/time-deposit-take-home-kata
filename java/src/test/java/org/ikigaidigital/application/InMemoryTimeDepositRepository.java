package org.ikigaidigital.application;

import java.util.ArrayList;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.TimeDepositRepository;

/** Second implementation of the port, so use cases are unit-testable without a database. */
class InMemoryTimeDepositRepository implements TimeDepositRepository {

  private final List<TimeDeposit> stored = new ArrayList<>();
  private final List<Double> savedBalances = new ArrayList<>();

  InMemoryTimeDepositRepository(TimeDeposit... deposits) {
    stored.addAll(List.of(deposits));
  }

  @Override
  public List<TimeDeposit> findAll() {
    return List.copyOf(stored);
  }

  @Override
  public List<TimeDeposit> findAllForUpdate() {
    return findAll();
  }

  @Override
  public void saveBalances(List<TimeDeposit> deposits) {
    deposits.forEach(deposit -> savedBalances.add(deposit.getBalance()));
  }

  List<Double> savedBalances() {
    return savedBalances;
  }
}
