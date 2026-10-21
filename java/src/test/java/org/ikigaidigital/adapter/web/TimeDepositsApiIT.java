package org.ikigaidigital.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ikigaidigital.adapter.web.api.model.TimeDepositDto;
import org.ikigaidigital.adapter.web.api.model.WithdrawalDto;
import org.ikigaidigital.config.DatabaseFixture;
import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.RestClient;

// End to end: the running app over HTTP, against PostgreSQL in Docker.
@IntegrationTest
@DisplayName("Time deposits API")
class TimeDepositsApiIT {

  @Value("${local.server.port}")
  int port;

  @Autowired JdbcClient jdbc;

  DatabaseFixture database;
  RestClient api;

  @BeforeEach
  void setUp() {
    database = new DatabaseFixture(jdbc);
    database.clear();
    api =
        RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {})
            .build();
  }

  @Nested
  @DisplayName("GET /time-deposits")
  class ListTimeDeposits {

    @Test
    void returnsEveryDepositWithItsWithdrawals() {
      database.insertTimeDeposit(2, "premium", 46, "5000.50");
      database.insertTimeDeposit(1, "basic", 31, "1200.00");
      database.insertWithdrawal(11, 2, "300.00", "2026-10-01");
      database.insertWithdrawal(10, 2, "100.25", "2026-09-15");

      ResponseEntity<TimeDepositDto[]> response =
          api.get().uri("/time-deposits").retrieve().toEntity(TimeDepositDto[].class);

      // A-7: deposits by id, withdrawals by date. Equality also checks two decimals.
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
                      new WithdrawalDto(10, new BigDecimal("100.25"), LocalDate.of(2026, 9, 15)),
                      new WithdrawalDto(11, new BigDecimal("300.00"), LocalDate.of(2026, 10, 1)))));
    }

    @Test
    void returnsAnEmptyListWhenNothingIsStored() {
      ResponseEntity<TimeDepositDto[]> response =
          api.get().uri("/time-deposits").retrieve().toEntity(TimeDepositDto[].class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEmpty();
    }
  }

  @Nested
  @DisplayName("POST /time-deposits/balance-updates")
  class UpdateBalances {

    @Test
    void appliesOneMonthOfInterestToEveryDeposit() {
      database.insertTimeDeposit(1, "basic", 31, "1200.00");
      database.insertTimeDeposit(2, "student", 31, "1200.00");
      database.insertTimeDeposit(3, "premium", 46, "1200.00");
      database.insertTimeDeposit(4, "gold", 46, "1200.00");
      database.insertTimeDeposit(5, "basic", 31, "18.00");

      ResponseEntity<Void> response = postBalanceUpdate();

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(database.balances())
          .containsExactly(
              entry(1, "1201.00"),
              entry(2, "1203.00"),
              entry(3, "1205.00"),
              entry(4, "1200.00"),
              entry(5, "18.01"));
    }

    // A-3: each call pays one more month; parallel calls must not overwrite each other.
    @Test
    void appliesOneMonthPerCallWhenFourCallsRunAtOnce() throws InterruptedException {
      database.insertTimeDeposit(1, "basic", 31, "1200.00");
      Callable<ResponseEntity<Void>> call = UpdateBalances.this::postBalanceUpdate;
      ExecutorService pool = Executors.newFixedThreadPool(4);

      try {
        assertThat(pool.invokeAll(Collections.nCopies(4, call)))
            .extracting(result -> result.get().getStatusCode())
            .containsOnly(HttpStatus.NO_CONTENT);
      } finally {
        pool.shutdownNow();
      }

      // 1200.00, then 1201.00, 1202.00, 1203.00 and 1204.00: four months of 1.00 each.
      assertThat(database.balances()).containsExactly(entry(1, "1204.00"));
    }

    private ResponseEntity<Void> postBalanceUpdate() {
      return api.post().uri("/time-deposits/balance-updates").retrieve().toBodilessEntity();
    }
  }

  @Nested
  @DisplayName("every response")
  class EveryResponse {

    // Balances must not stay in browser or proxy caches; browsers must not guess content types.
    @ParameterizedTest(name = "{0} {1} is no-store and nosniff")
    @CsvSource({"GET, /time-deposits", "POST, /time-deposits/balance-updates"})
    void forbidsCachingAndContentSniffing(String method, String path) {
      ResponseEntity<Void> response =
          api.method(HttpMethod.valueOf(method)).uri(path).retrieve().toBodilessEntity();

      assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
      assertThat(response.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
    }

    @Test
    void hidesInternalsWhenARequestFails() {
      ResponseEntity<String> response =
          api.delete().uri("/time-deposits").retrieve().toEntity(String.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
      assertThat(response.getBody()).doesNotContain("trace", "exception", "org.springframework");
    }
  }
}
