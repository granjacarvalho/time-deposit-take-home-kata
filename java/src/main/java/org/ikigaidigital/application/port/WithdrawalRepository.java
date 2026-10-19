package org.ikigaidigital.application.port;

import java.util.List;
import org.ikigaidigital.Withdrawal;

/** Driven port for the withdrawals table, owned by the application. */
public interface WithdrawalRepository {

  /** Every withdrawal, ordered by deposit, then date, then id (A-7). */
  List<Withdrawal> findAll();
}
