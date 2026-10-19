package org.ikigaidigital.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@IntegrationTest
class TimeDepositApplicationIT {

  @Autowired ApplicationContext context;

  @Test
  void should_start_when_database_is_available() {
    assertThat(context).isNotNull();
  }
}
