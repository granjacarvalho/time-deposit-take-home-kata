package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.UpdateAllBalances;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires the Spring-free domain and use cases by hand (DIP: adapters depend on the ports). */
@Configuration(proxyBeanMethods = false)
class UseCaseConfig {

  @Bean
  TimeDepositCalculator timeDepositCalculator() {
    return new TimeDepositCalculator();
  }

  @Bean
  UpdateAllBalances updateAllBalances(
      TimeDepositRepository repository, TimeDepositCalculator calculator) {
    return new UpdateAllBalances(repository, calculator);
  }
}
