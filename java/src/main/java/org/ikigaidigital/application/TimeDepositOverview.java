package org.ikigaidigital.application;

import java.math.BigDecimal;
import java.util.List;
import org.ikigaidigital.Withdrawal;

/** What the list endpoint returns for one deposit. */
public record TimeDepositOverview(
    int id, String planType, BigDecimal balance, int days, List<Withdrawal> withdrawals) {}
