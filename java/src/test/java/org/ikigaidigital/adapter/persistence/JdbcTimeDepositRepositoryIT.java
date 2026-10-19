package org.ikigaidigital.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.config.DatabaseFixture;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

@IntegrationTest
@DisplayName("AC-8 balances survive the NUMERIC <-> legacy double round trip")
class JdbcTimeDepositRepositoryIT {

  @Autowired TimeDepositRepository repository;
  @Autowired JdbcClient jdbc;

  DatabaseFixture database;

  @BeforeEach
  void setUp() {
    database = new DatabaseFixture(jdbc);
    database.clear();
  }

  @ParameterizedTest(name = "stored {0} loads as double {0}")
  @CsvSource({"0.01", "18.01", "1234567.89", "99999999999.99"})
  void should_load_balance_as_legacy_double_when_found(String balance) {
    database.timeDeposit(1, "basic", 0, balance);

    var deposits = repository.findAll();

    assertThat(deposits)
        .singleElement()
        .extracting(TimeDeposit::getBalance)
        .isEqualTo(Double.valueOf(balance));
  }

  @ParameterizedTest(name = "double {0} is stored as {0}")
  @CsvSource({"0.01", "18.01", "1234567.89", "99999999999.99"})
  void should_store_balance_without_drift_when_saved(String balance) {
    database.timeDeposit(1, "basic", 0, "0.00");
    var deposits = repository.findAll();
    deposits.forEach(deposit -> deposit.setBalance(Double.valueOf(balance)));

    repository.saveBalances(deposits);

    assertThat(database.balances()).containsExactly(entry(1, balance));
  }
}
