package org.ikigaidigital.application;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import org.ikigaidigital.Money;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.Withdrawal;
import org.ikigaidigital.application.port.TimeDepositRepository;
import org.ikigaidigital.application.port.WithdrawalRepository;

/** Every stored deposit with its withdrawals, deposits ordered by id (A-7). */
public class ListTimeDeposits {

  private final TimeDepositRepository deposits;
  private final WithdrawalRepository withdrawals;

  public ListTimeDeposits(TimeDepositRepository deposits, WithdrawalRepository withdrawals) {
    this.deposits = deposits;
    this.withdrawals = withdrawals;
  }

  public List<TimeDepositOverview> listAll() {
    Map<Integer, List<Withdrawal>> withdrawalsByDeposit =
        withdrawals.findAll().stream().collect(groupingBy(Withdrawal::timeDepositId));
    return deposits.findAll().stream()
        .map(deposit -> overview(deposit, withdrawalsByDeposit))
        .toList();
  }

  private static TimeDepositOverview overview(
      TimeDeposit deposit, Map<Integer, List<Withdrawal>> withdrawalsByDeposit) {
    return new TimeDepositOverview(
        deposit.getId(),
        deposit.getPlanType(),
        Money.fromLegacy(deposit.getBalance()),
        deposit.getDays(),
        withdrawalsByDeposit.getOrDefault(deposit.getId(), List.of()));
  }
}
