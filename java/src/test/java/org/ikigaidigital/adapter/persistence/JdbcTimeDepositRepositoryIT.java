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
@DisplayName("JdbcTimeDepositRepository keeps every cent between NUMERIC and the legacy double")
class JdbcTimeDepositRepositoryIT {

  @Autowired TimeDepositRepository repository;
  @Autowired JdbcClient jdbc;

  DatabaseFixture database;

  @BeforeEach
  void setUp() {
    database = new DatabaseFixture(jdbc);
    database.clear();
  }

  @ParameterizedTest(name = "stored {0} loads as {0}")
  @CsvSource({"0.01", "18.01", "1234567.89", "9999999999999.99"})
  void loadsAStoredBalanceAsTheSameDouble(String balance) {
    database.insertTimeDeposit(1, "basic", 0, balance);

    var deposits = repository.findAll();

    assertThat(deposits)
        .singleElement()
        .extracting(TimeDeposit::getBalance)
        .isEqualTo(Double.valueOf(balance));
  }

  @ParameterizedTest(name = "{0} is saved as {0}")
  @CsvSource({"0.01", "18.01", "1234567.89", "9999999999999.99"})
  void savesABalanceWithoutLosingACent(String balance) {
    database.insertTimeDeposit(1, "basic", 0, "0.00");
    var deposits = repository.findAll();
    deposits.forEach(deposit -> deposit.setBalance(Double.valueOf(balance)));

    repository.saveBalances(deposits);

    assertThat(database.balances()).containsExactly(entry(1, balance));
  }
}
