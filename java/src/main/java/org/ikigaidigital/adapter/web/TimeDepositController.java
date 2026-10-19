package org.ikigaidigital.adapter.web;

import java.util.List;
import org.ikigaidigital.Withdrawal;
import org.ikigaidigital.adapter.web.api.TimeDepositsApi;
import org.ikigaidigital.adapter.web.api.model.TimeDepositDto;
import org.ikigaidigital.adapter.web.api.model.WithdrawalDto;
import org.ikigaidigital.application.ListTimeDeposits;
import org.ikigaidigital.application.TimeDepositOverview;
import org.ikigaidigital.application.UpdateAllBalances;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

/** Implements the two endpoints of the OpenAPI contract and nothing else. */
@RestController
class TimeDepositController implements TimeDepositsApi {

  private final ListTimeDeposits listTimeDeposits;
  private final UpdateAllBalances updateAllBalances;

  TimeDepositController(ListTimeDeposits listTimeDeposits, UpdateAllBalances updateAllBalances) {
    this.listTimeDeposits = listTimeDeposits;
    this.updateAllBalances = updateAllBalances;
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<List<TimeDepositDto>> listTimeDeposits() {
    return ResponseEntity.ok(
        listTimeDeposits.listAll().stream().map(TimeDepositController::toDto).toList());
  }

  @Override
  @Transactional
  public ResponseEntity<Void> updateBalances() {
    updateAllBalances.updateAll();
    return ResponseEntity.noContent().build();
  }

  private static TimeDepositDto toDto(TimeDepositOverview deposit) {
    return new TimeDepositDto(
        deposit.id(),
        deposit.planType(),
        deposit.balance(),
        deposit.days(),
        deposit.withdrawals().stream().map(TimeDepositController::toDto).toList());
  }

  private static WithdrawalDto toDto(Withdrawal withdrawal) {
    return new WithdrawalDto(withdrawal.id(), withdrawal.amount(), withdrawal.date());
  }
}
