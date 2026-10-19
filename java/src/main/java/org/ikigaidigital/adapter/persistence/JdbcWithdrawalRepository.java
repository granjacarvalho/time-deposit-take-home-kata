package org.ikigaidigital.adapter.persistence;

import java.time.LocalDate;
import java.util.List;
import org.ikigaidigital.Withdrawal;
import org.ikigaidigital.application.port.WithdrawalRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcWithdrawalRepository implements WithdrawalRepository {

  private final JdbcClient jdbc;

  JdbcWithdrawalRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public List<Withdrawal> findAll() {
    return jdbc.sql(
            """
            SELECT id, timeDepositId, amount, date FROM withdrawals
            ORDER BY timeDepositId, date, id""")
        .query(
            (rs, row) ->
                new Withdrawal(
                    rs.getInt("id"),
                    rs.getInt("timeDepositId"),
                    rs.getBigDecimal("amount"),
                    rs.getObject("date", LocalDate.class)))
        .list();
  }
}
