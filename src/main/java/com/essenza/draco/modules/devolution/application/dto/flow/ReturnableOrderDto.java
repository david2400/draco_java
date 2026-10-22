package com.essenza.draco.modules.devolution.application.dto.flow;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/** Líneas de la orden con lo vendido, lo ya devuelto y lo que aún se puede devolver. */
public record ReturnableOrderDto(
        Long orderId,
        String orderState,
        @Schema(description = "La orden admite devoluciones (pagada, en proceso, enviada o entregada)") boolean returnable,
        BigDecimal orderTotal,
        BigDecimal returnedAmount,
        @Schema(description = "Lo que todavía se puede reembolsar de los pagos") BigDecimal refundable,
        List<Line> lines) {

    @Schema(name = "ReturnableLine")
    public record Line(Long productOrderId, Long productId, Long skuId, String skuCode, String productName,
                       int sold, int returned, int returnable, BigDecimal unitPrice, BigDecimal total) {
    }
}
