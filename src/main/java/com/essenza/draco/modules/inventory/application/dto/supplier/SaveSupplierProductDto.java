package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** SKU que surte el proveedor. */
public record SaveSupplierProductDto(
        @NotNull Long skuId,
        @Size(max = 64) @Schema(description = "Referencia del producto en el catálogo del proveedor") String supplierCode,
        @NotNull @Schema(description = "Costo de compra unitario") BigDecimal unitCost,
        @Schema(description = "Días de entrega de este producto (si difiere del general)") Integer leadTimeDays,
        @Schema(description = "Cantidad mínima de compra (por defecto 1)") Integer minOrderQty,
        @Schema(description = "Proveedor preferido del SKU (desmarca a los demás)") Boolean preferred,
        Boolean active) {
}
