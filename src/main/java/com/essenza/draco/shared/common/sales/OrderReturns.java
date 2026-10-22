package com.essenza.draco.shared.common.sales;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Contrato entre devoluciones y ventas: líneas de la orden que se pueden devolver y
 * reembolso sobre sus pagos. Lo implementa ventas; se ejecuta en la transacción de quien llama.
 */
public interface OrderReturns {

    /** Orden con sus líneas (precio unitario efectivo = total de la línea / cantidad). */
    Optional<ReturnableOrder> find(Long orderId);

    /**
     * Acredita {@code creditValue} a la orden (lo que se cobra pasa a ser total − devuelto) y
     * reembolsa {@code amount} sobre sus pagos aprobados, del más reciente al más antiguo.
     *
     * @throws com.essenza.draco.shared.exceptions.ConflictException si los pagos no alcanzan
     */
    RefundResult refund(Long orderId, BigDecimal creditValue, BigDecimal amount, String reason);

    /** Lo que todavía se puede reembolsar de los pagos de la orden. */
    BigDecimal refundable(Long orderId);

    record ReturnableOrder(Long id, String state, BigDecimal total, BigDecimal returnedAmount,
                           List<ReturnableLine> lines) {
    }

    record ReturnableLine(Long lineId, Long productId, Long skuId, String skuCode, String productName, int quantity,
                          BigDecimal unitPrice, BigDecimal total) {
    }

    record RefundResult(BigDecimal refunded, List<RefundedPayment> payments) {
    }

    record RefundedPayment(Long paymentId, String reference, String method, BigDecimal amount) {
    }
}
