package org.ikigaidigital.adapter.persistence;

import java.util.List;
import org.ikigaidigital.Money;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcTimeDepositRepository implements TimeDepositRepository {

  private final JdbcClient jdbc;

  JdbcTimeDepositRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public List<TimeDeposit> findAll() {
    return jdbc.sql("SELECT id, planType, days, balance FROM timeDeposits ORDER BY id")
        .query(
            (rs, row) ->
                new TimeDeposit(
                    rs.getInt("id"),
                    rs.getString("planType"),
                    rs.getBigDecimal("balance").doubleValue(),
                    rs.getInt("days")))
        .list();
  }

  // A-5: the legacy double becomes NUMERIC(19, 2) only here, at the edge.
  @Override
  public void saveBalances(List<TimeDeposit> deposits) {
    deposits.forEach(
        deposit ->
            jdbc.sql("UPDATE timeDeposits SET balance = ? WHERE id = ?")
                .params(Money.fromLegacy(deposit.getBalance()), deposit.getId())
                .update());
  }
}
