package org.ikigaidigital.config;

import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.simple.JdbcClient;

/** Test data straight through SQL, so tests do not depend on the code under test to arrange. */
public final class DatabaseFixture {

  private final JdbcClient jdbc;

  public DatabaseFixture(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public void clear() {
    jdbc.sql("DELETE FROM withdrawals").update();
    jdbc.sql("DELETE FROM timeDeposits").update();
  }

  public void timeDeposit(int id, String planType, int days, String balance) {
    jdbc.sql("INSERT INTO timeDeposits (id, planType, days, balance) VALUES (?, ?, ?, ?)")
        .params(id, planType, days, new BigDecimal(balance))
        .update();
  }

  public void withdrawal(int id, int timeDepositId, String amount, String date) {
    jdbc.sql("INSERT INTO withdrawals (id, timeDepositId, amount, date) VALUES (?, ?, ?, ?)")
        .params(id, timeDepositId, new BigDecimal(amount), LocalDate.parse(date))
        .update();
  }

  /** id -> balance as stored, e.g. "1201.00". */
  public Map<Integer, String> balances() {
    return jdbc
        .sql("SELECT id, balance FROM timeDeposits ORDER BY id")
        .query((rs, row) -> Map.entry(rs.getInt("id"), rs.getBigDecimal("balance").toPlainString()))
        .list()
        .stream()
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
  }
}
