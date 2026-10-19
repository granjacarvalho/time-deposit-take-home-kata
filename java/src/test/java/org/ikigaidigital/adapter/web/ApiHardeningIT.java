package org.ikigaidigital.adapter.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@IntegrationTest
@DisplayName("Security: responses carry no cacheable money and no internals")
class ApiHardeningIT {

  @Value("${local.server.port}")
  int port;

  // Balances must not stay in browser or proxy caches; browsers must not guess content types.
  @ParameterizedTest(name = "{0} {1} is no-store and nosniff")
  @CsvSource({"GET, /time-deposits", "POST, /time-deposits/balance-updates"})
  void should_forbid_caching_and_sniffing_when_endpoint_answers(String method, String path) {
    ResponseEntity<String> response = call(HttpMethod.valueOf(method), path);

    assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
    assertThat(response.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
  }

  @Test
  void should_hide_internals_when_request_fails() {
    ResponseEntity<String> response = call(HttpMethod.DELETE, "/time-deposits");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    assertThat(response.getBody()).doesNotContain("trace", "exception", "org.springframework");
    assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
  }

  private ResponseEntity<String> call(HttpMethod method, String path) {
    return RestClient.create("http://localhost:" + port)
        .method(method)
        .uri(path)
        .retrieve()
        .onStatus(status -> true, (request, failedResponse) -> {})
        .toEntity(String.class);
  }
}
