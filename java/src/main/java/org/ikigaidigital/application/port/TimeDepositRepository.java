package org.ikigaidigital.application.port;

import java.util.List;
import org.ikigaidigital.TimeDeposit;

/** Driven port for the timeDeposits table, owned by the application. */
public interface TimeDepositRepository {

  /** Every stored deposit, ordered by id. */
  List<TimeDeposit> findAll();

  /**
   * Every stored deposit, ordered by id, locked until the transaction ends, so concurrent updates
   * apply one after another instead of overwriting each other.
   */
  List<TimeDeposit> findAllForUpdate();

  /** Persists each deposit's balance. */
  void saveBalances(List<TimeDeposit> deposits);
}
