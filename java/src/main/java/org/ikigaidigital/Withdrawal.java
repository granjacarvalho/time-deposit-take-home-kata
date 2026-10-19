package org.ikigaidigital;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A withdrawal from a time deposit. A-2: read-only here; the stored balance is already net of it
 * and it does not change interest.
 */
public record Withdrawal(int id, int timeDepositId, BigDecimal amount, LocalDate date) {}
