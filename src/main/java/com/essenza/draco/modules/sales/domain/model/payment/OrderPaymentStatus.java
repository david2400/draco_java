package com.essenza.draco.modules.sales.domain.model.payment;

import java.math.BigDecimal;
import java.util.List;

/** Estado de cobro de una orden según sus pagos. */
public enum OrderPaymentStatus {
    /** Sin pagos aprobados. */
    UNPAID,
    /** Pagada en parte. */
    PARTIAL,
    /** Lo aprobado cubre el total. */
    PAID,
    /** Lo aprobado supera el total (hay que devolver la diferencia). */
    OVERPAID;

    /** Totales de cobro de una orden. */
    public record Balance(BigDecimal total, BigDecimal settled, BigDecimal pending, BigDecimal balance,
                          OrderPaymentStatus status) {

        /** Lo que todavía se puede cobrar sin duplicar pagos en curso. */
        public BigDecimal collectable() {
            BigDecimal value = balance.subtract(pending);
            return value.signum() < 0 ? BigDecimal.ZERO : value;
        }
    }

    /** Con devoluciones: una orden devuelta por completo y sin saldo queda saldada (PAID). */
    public static Balance of(BigDecimal orderTotal, BigDecimal returned, List<Payment> payments) {
        Balance balance = of(orderTotal, payments);
        boolean hasReturns = returned != null && returned.signum() > 0;
        if (hasReturns && balance.status() == UNPAID && balance.total().signum() <= 0) {
            return new Balance(balance.total(), balance.settled(), balance.pending(), balance.balance(), PAID);
        }
        return balance;
    }

    public static Balance of(BigDecimal orderTotal, List<Payment> payments) {
        BigDecimal total = orderTotal == null ? BigDecimal.ZERO : orderTotal;
        BigDecimal settled = payments.stream().map(Payment::settledAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pending = payments.stream().map(Payment::pendingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balance = total.subtract(settled);
        OrderPaymentStatus status;
        if (settled.signum() <= 0) {
            status = UNPAID;
        } else if (settled.compareTo(total) < 0) {
            status = PARTIAL;
        } else if (settled.compareTo(total) == 0) {
            status = PAID;
        } else {
            status = OVERPAID;
        }
        return new Balance(total, settled, pending, balance.signum() < 0 ? BigDecimal.ZERO : balance, status);
    }
}
