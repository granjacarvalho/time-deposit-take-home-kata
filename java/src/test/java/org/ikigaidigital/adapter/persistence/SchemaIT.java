package org.ikigaidigital.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.ikigaidigital.config.DatabaseFixture;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

// PostgreSQL stores the brief's unquoted camelCase names in lower case (A-6).
@IntegrationTest
@DisplayName("AC-7 the tables match the brief")
class SchemaIT {

  @Autowired JdbcClient jdbc;

  @BeforeEach
  void setUp() {
    new DatabaseFixture(jdbc).clear();
  }

  @ParameterizedTest(name = "{0}.{1} is {2}, nullable {3}")
  @CsvSource(
      useHeadersInDisplayName = true,
      textBlock =
          """
          table,        column,        type,              nullable
          timedeposits, id,            integer,           NO
          timedeposits, plantype,      character varying, NO
          timedeposits, days,          integer,           NO
          timedeposits, balance,       numeric,           NO
          withdrawals,  id,            integer,           NO
          withdrawals,  timedepositid, integer,           NO
          withdrawals,  amount,        numeric,           NO
          withdrawals,  date,          date,              NO
          """)
  void should_define_column_as_specified(
      String table, String column, String type, String nullable) {
    var definition =
        jdbc.sql(
                """
                SELECT data_type || ',' || is_nullable FROM information_schema.columns
                WHERE table_name = ? AND column_name = ?""")
            .params(table, column)
            .query(String.class)
            .list();

    assertThat(definition).containsExactly(type + "," + nullable);
  }

  @ParameterizedTest(name = "{0} has primary key id")
  @CsvSource({"timedeposits", "withdrawals"})
  void should_use_id_as_primary_key(String table) {
    var keyColumns =
        jdbc.sql(
                """
                SELECT k.column_name FROM information_schema.table_constraints c
                JOIN information_schema.key_column_usage k ON k.constraint_name = c.constraint_name
                WHERE c.table_name = ? AND c.constraint_type = 'PRIMARY KEY'""")
            .param(table)
            .query(String.class)
            .list();

    assertThat(keyColumns).containsExactly("id");
  }

  @Test
  void should_reject_withdrawal_when_time_deposit_does_not_exist() {
    assertThatThrownBy(
            () ->
                jdbc.sql(
                        """
                        INSERT INTO withdrawals (timeDepositId, amount, date)
                        VALUES (999999, 1.00, DATE '2026-10-01')""")
                    .update())
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("foreign key");
  }

  @ParameterizedTest(name = "rejects a time deposit with {0}")
  @CsvSource(
      useHeadersInDisplayName = true,
      delimiter = '|',
      textBlock =
          """
          rule                                   | planType | days | balance
          a negative balance                     | basic    | 0    | -0.01
          negative days                          | basic    | -1   | 0.00
          a balance beyond exact double cents    | basic    | 0    | 10000000000000.00
          """)
  void should_reject_time_deposit_when_it_breaks_a_rule(
      String rule, String planType, int days, BigDecimal balance) {
    assertThatThrownBy(() -> insertTimeDeposit(planType, days, balance))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @ParameterizedTest(name = "rejects a withdrawal of {0}")
  @CsvSource({"0.00", "-0.01"})
  void should_reject_withdrawal_when_amount_is_not_positive(BigDecimal amount) {
    int timeDepositId = insertTimeDeposit("basic", 0, BigDecimal.ZERO);

    assertThatThrownBy(
            () ->
                jdbc.sql(
                        """
                        INSERT INTO withdrawals (timeDepositId, amount, date)
                        VALUES (?, ?, DATE '2026-10-01')""")
                    .params(timeDepositId, amount)
                    .update())
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @ParameterizedTest(name = "accepts balance {0}")
  @CsvSource({"0.00", "9999999999999.99"})
  void should_accept_time_deposit_when_balance_is_at_a_limit(BigDecimal balance) {
    assertThat(insertTimeDeposit("basic", 0, balance)).isPositive();
  }

  private int insertTimeDeposit(String planType, int days, BigDecimal balance) {
    return jdbc.sql(
            "INSERT INTO timeDeposits (planType, days, balance) VALUES (?, ?, ?) RETURNING id")
        .params(planType, days, balance)
        .query(Integer.class)
        .single();
  }
}
