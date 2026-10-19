package org.ikigaidigital.adapter.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcTimeDepositRepository implements TimeDepositRepository {

  private static final int CENTS_SCALE = 2;

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

  @Override
  public void saveBalances(List<TimeDeposit> deposits) {
    deposits.forEach(
        deposit ->
            jdbc.sql("UPDATE timeDeposits SET balance = ? WHERE id = ?")
                .params(toMoney(deposit.getBalance()), deposit.getId())
                .update());
  }

  // A-5: the legacy double becomes NUMERIC(19, 2) here, at the edge. valueOf uses the shortest
  // decimal that round-trips, so 1235595.81 stays 1235595.81; exact for balances below 10^13.
  private static BigDecimal toMoney(double balance) {
    return BigDecimal.valueOf(balance).setScale(CENTS_SCALE, RoundingMode.HALF_UP);
  }
}
