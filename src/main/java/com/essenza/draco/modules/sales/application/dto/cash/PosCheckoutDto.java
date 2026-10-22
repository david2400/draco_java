package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Venta de mostrador: crea la orden, sus líneas y los pagos en una sola operación. */
public record PosCheckoutDto(
        @NotNull Long cashSessionId,
        @NotEmpty @Valid List<Item> items,
        @NotEmpty @Valid List<PaymentLine> payments,
        @Size(max = 200) @Schema(description = "Referencia libre (p. ej. nombre del cliente)") String reference,
        @Valid @Schema(description = "Comprador para la factura automática (vacío = consumidor final)") Buyer buyer) {

    public PosCheckoutDto(Long cashSessionId, List<Item> items, List<PaymentLine> payments, String reference) {
        this(cashSessionId, items, payments, reference, null);
    }

    @Schema(name = "PosBuyer")
    public record Buyer(@Size(max = 10) String docType, @Size(max = 30) String docNumber, @Size(max = 200) String name,
                        @Size(max = 120) String email, @Size(max = 40) String phone) {
    }


    public record Item(Long productId, Long skuId, @NotNull @Min(1) Integer quantity, @DecimalMin("0") BigDecimal discount) {
    }

    public record PaymentLine(@NotNull Long paymentMethodId, @NotNull @DecimalMin("0.01") BigDecimal amount,
                              @Schema(description = "Efectivo recibido") BigDecimal tenderedAmount,
                              @Size(max = 120) String reference) {
    }
}
