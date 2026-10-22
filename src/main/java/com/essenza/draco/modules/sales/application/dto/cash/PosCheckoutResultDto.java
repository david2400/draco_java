package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.util.List;

import com.essenza.draco.modules.sales.application.dto.payment.PaymentDto;

public record PosCheckoutResultDto(Long orderId, String orderState, BigDecimal total, BigDecimal taxAmount,
                                   BigDecimal netAmount, BigDecimal changeAmount, List<PaymentDto> payments,
                                   @io.swagger.v3.oas.annotations.media.Schema(description = "La caja factura sola: la factura se emite tras la venta")
                                   Boolean autoInvoice) {
}
