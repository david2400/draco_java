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
