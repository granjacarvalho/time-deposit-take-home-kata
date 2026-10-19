package org.ikigaidigital.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.ikigaidigital.config.DatabaseFixture;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.RestClient;

@IntegrationTest
@DisplayName(
    "AC-8 POST /time-deposits/balance-updates applies a month of interest to every deposit")
class UpdateBalancesIT {

  @Value("${local.server.port}")
  int port;

  @Autowired JdbcClient jdbc;

  DatabaseFixture database;

  @BeforeEach
  void setUp() {
    database = new DatabaseFixture(jdbc);
    database.clear();
  }

  @Test
  void should_apply_monthly_interest_to_every_deposit_when_balances_are_updated() {
    database.timeDeposit(1, "basic", 31, "1200.00");
    database.timeDeposit(2, "student", 31, "1200.00");
    database.timeDeposit(3, "premium", 46, "1200.00");
    database.timeDeposit(4, "gold", 46, "1200.00");
    database.timeDeposit(5, "basic", 31, "18.00");

    var response =
        RestClient.create("http://localhost:" + port)
            .post()
            .uri("/time-deposits/balance-updates")
            .retrieve()
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(database.balances())
        .containsExactly(
            entry(1, "1201.00"),
            entry(2, "1203.00"),
            entry(3, "1205.00"),
            entry(4, "1200.00"),
            entry(5, "18.01"));
  }
}
