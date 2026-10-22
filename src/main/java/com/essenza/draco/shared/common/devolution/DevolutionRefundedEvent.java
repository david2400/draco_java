package com.essenza.draco.shared.common.devolution;

import java.math.BigDecimal;
import java.util.List;

/**
 * Una devolución quedó reembolsada (se publica dentro de la transacción; escuchar tras el commit).
 * Las líneas traen la cantidad recibida y el valor acreditado (con impuesto incluido).
 */
public record DevolutionRefundedEvent(Long devolutionId, Long orderId, String reason, List<Line> lines) {

    public record Line(Long orderLineId, int quantity, BigDecimal amount) {
    }
}
