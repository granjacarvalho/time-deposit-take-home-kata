package org.ikigaidigital.config;

import org.springframework.boot.SpringApplication;

// Local run without a compose file: `./mvnw spring-boot:test-run` starts PostgreSQL in Docker.
public class TestTimeDepositApplication {

  public static void main(String[] args) {
    SpringApplication.from(TimeDepositApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
