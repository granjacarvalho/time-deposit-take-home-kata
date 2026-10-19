package org.ikigaidigital.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Lives outside the domain packages so Spring never leaks into them; adapters are scanned
// explicitly.
@SpringBootApplication(scanBasePackages = {"org.ikigaidigital.config", "org.ikigaidigital.adapter"})
public class TimeDepositApplication {

  public static void main(String[] args) {
    SpringApplication.run(TimeDepositApplication.class, args);
  }
}
