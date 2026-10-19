package org.ikigaidigital.adapter.persistence;

import java.util.List;
import org.ikigaidigital.Money;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcTimeDepositRepository implements TimeDepositRepository {

  private final JdbcTemplate jdbcTemplate;
  private final JdbcClient jdbc;

  JdbcTimeDepositRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbc = JdbcClient.create(jdbcTemplate);
  }

  @Override
  public List<TimeDeposit> findAll() {
    return query("SELECT id, planType, days, balance FROM timeDeposits ORDER BY id");
  }

  @Override
  public List<TimeDeposit> findAllForUpdate() {
    return query("SELECT id, planType, days, balance FROM timeDeposits ORDER BY id FOR UPDATE");
  }

  // A-5: the legacy double becomes NUMERIC(19, 2) only here, at the edge.
  @Override
  public void saveBalances(List<TimeDeposit> deposits) {
    jdbcTemplate.batchUpdate(
        "UPDATE timeDeposits SET balance = ? WHERE id = ?",
        deposits.stream()
            .map(deposit -> new Object[] {Money.fromLegacy(deposit.getBalance()), deposit.getId()})
            .toList());
  }

  private List<TimeDeposit> query(String sql) {
    return jdbc.sql(sql)
        .query(
            (rs, row) ->
                new TimeDeposit(
                    rs.getInt("id"),
                    rs.getString("planType"),
                    rs.getBigDecimal("balance").doubleValue(),
                    rs.getInt("days")))
        .list();
  }
}
