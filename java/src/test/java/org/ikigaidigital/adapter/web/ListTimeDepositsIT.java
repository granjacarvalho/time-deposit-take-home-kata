package org.ikigaidigital.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.ikigaidigital.adapter.web.api.model.TimeDepositDto;
import org.ikigaidigital.adapter.web.api.model.WithdrawalDto;
import org.ikigaidigital.config.DatabaseFixture;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.RestClient;

@IntegrationTest
@DisplayName("AC-9 GET /time-deposits returns every deposit with its withdrawals")
class ListTimeDepositsIT {

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
  void should_return_every_deposit_with_its_withdrawals_when_listed() {
    database.timeDeposit(2, "premium", 46, "5000.50");
    database.timeDeposit(1, "basic", 31, "1200.00");
    database.withdrawal(11, 2, "300.00", "2026-10-01");
    database.withdrawal(10, 2, "100.25", "2026-09-15");

    ResponseEntity<TimeDepositDto[]> response = get();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    // A-7: deposits by id, withdrawals by date then id. Recursive equals also checks 2 decimals.
    assertThat(response.getBody())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(
            new TimeDepositDto(1, "basic", new BigDecimal("1200.00"), 31, List.of()),
            new TimeDepositDto(
                2,
                "premium",
                new BigDecimal("5000.50"),
                46,
                List.of(
                    new WithdrawalDto(10, new BigDecimal("100.25"), LocalDate.parse("2026-09-15")),
                    new WithdrawalDto(
                        11, new BigDecimal("300.00"), LocalDate.parse("2026-10-01")))));
  }

  @Test
  void should_return_empty_list_when_no_deposits_are_stored() {
    ResponseEntity<TimeDepositDto[]> response = get();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  private ResponseEntity<TimeDepositDto[]> get() {
    return RestClient.create("http://localhost:" + port)
        .get()
        .uri("/time-deposits")
        .retrieve()
        .onStatus(status -> true, (request, failedResponse) -> {})
        .toEntity(TimeDepositDto[].class);
  }
}
