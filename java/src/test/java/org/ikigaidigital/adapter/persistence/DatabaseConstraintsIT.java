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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

// Integrity lives in the schema (V1 tables, V2 checks), so it holds for every writer, not only
// for this application.
@IntegrationTest
@DisplayName("The database rejects data that breaks the rules")
class DatabaseConstraintsIT {

  @Autowired JdbcClient jdbc;

  DatabaseFixture database;

  @BeforeEach
  void setUp() {
    database = new DatabaseFixture(jdbc);
    database.clear();
  }

  @ParameterizedTest(name = "rejects a time deposit with {0}")
  @CsvSource(
      delimiter = '|',
      textBlock =
          """
          no plan type                       |       | 0  | 0.00
          a negative balance                 | basic | 0  | -0.01
          negative days                      | basic | -1 | 0.00
          a balance of 10^13 or more (A-5)   | basic | 0  | 10000000000000.00
          """)
  void rejectsAnInvalidTimeDeposit(String rule, String planType, int days, BigDecimal balance) {
    assertThatThrownBy(() -> database.insertTimeDeposit(planType, days, balance))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @ParameterizedTest(name = "accepts a balance of {0}")
  @ValueSource(strings = {"0.00", "9999999999999.99"})
  void acceptsABalanceAtTheLimits(BigDecimal balance) {
    int id = database.insertTimeDeposit("basic", 0, balance);

    assertThat(database.balances()).containsEntry(id, balance.toPlainString());
  }

  @Test
  void rejectsAWithdrawalForAnUnknownDeposit() {
    assertThatThrownBy(() -> database.insertWithdrawal(999999, new BigDecimal("1.00")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  // A-2: a withdrawal is money taken out, so its amount is positive.
  @ParameterizedTest(name = "rejects a withdrawal of {0}")
  @ValueSource(strings = {"0.00", "-0.01"})
  void rejectsANonPositiveWithdrawal(BigDecimal amount) {
    int timeDepositId = database.insertTimeDeposit("basic", 0, new BigDecimal("100.00"));

    assertThatThrownBy(() -> database.insertWithdrawal(timeDepositId, amount))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
