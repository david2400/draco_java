package com.essenza.draco.modules.sales.application.output.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Persistencia de órdenes y líneas para los flujos con stock (Fase 5). */
public interface OrderLedger {

    record OrderHeader(Long id, String state, boolean stockManaged, Instant createdAt) {
    }

    record OrderLine(Long id, Long orderId, Long productId, Long skuId, String skuCode, String productName,
                     BigDecimal unitPrice, int quantity, BigDecimal discount, BigDecimal subtotal, BigDecimal total) {
    }

    /** Carga la orden bloqueándola (serializa cambios de líneas y de estado). */
    Optional<OrderHeader> lockOrder(Long orderId);

    /** Crea una orden PENDING, con total 0, que gestiona stock. */
    Long createOrder(String complementaryOrder);

    void updateComplementary(Long orderId, String complementaryOrder);

    void setState(Long orderId, String state, String cancelReason);

    /** total = suma de las líneas vigentes. */
    void refreshTotal(Long orderId);

    Optional<OrderLine> findLine(Long lineId);

    List<OrderLine> lines(Long orderId);

    OrderLine saveLine(OrderLine line);

    void deleteLine(Long lineId);

    /** Órdenes con stock gestionado, PENDING y creadas antes de {@code createdBefore}. */
    List<Long> expiredPendingOrders(Instant createdBefore);
}
