package org.ikigaidigital.adapter.web;

import java.util.List;
import org.ikigaidigital.adapter.web.api.TimeDepositsApi;
import org.ikigaidigital.adapter.web.api.model.TimeDepositDto;
import org.ikigaidigital.application.UpdateAllBalances;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TimeDepositController implements TimeDepositsApi {

  private final UpdateAllBalances updateAllBalances;

  TimeDepositController(UpdateAllBalances updateAllBalances) {
    this.updateAllBalances = updateAllBalances;
  }

  @Override
  public ResponseEntity<List<TimeDepositDto>> listTimeDeposits() {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @Override
  @Transactional
  public ResponseEntity<Void> updateBalances() {
    updateAllBalances.updateAll();
    return ResponseEntity.noContent().build();
  }
}
