package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos y reglas de una caja. */
public record SaveCashRegisterDto(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 160) String location,
        @Schema(description = "Bodega propia (vacía = la del punto de venta)") Long warehouseId,
        Boolean active,
        @NotNull @Schema(description = "Punto de venta al que pertenece") Long storeId,
        @Size(max = 80) @Schema(description = "Equipo o terminal (opcional)") String deviceName,
        @Schema(description = "Medios de pago permitidos (vacío = todos los activos)") List<Long> allowedMethodIds,
        @Schema(description = "FREE, SUGGESTED o FIXED") String openingMode,
        @DecimalMin("0") BigDecimal defaultOpeningAmount,
        @DecimalMin("0.01") @Schema(description = "Tope de efectivo: sobre este valor se pide un retiro") BigDecimal cashLimit,
        @Schema(description = "El cajero cuenta sin ver el esperado") Boolean blindClose,
        @DecimalMin("0") @Schema(description = "Diferencia máxima sin justificación al cerrar") BigDecimal differenceTolerance,
        @DecimalMin("0") @DecimalMax("100") @Schema(description = "Descuento máximo por línea (%)") BigDecimal maxDiscountPercent,
        @Schema(description = "Factura electrónica automática en cada venta") Boolean autoInvoice,
        @Schema(description = "58MM u 80MM") String receiptWidth) {
}
