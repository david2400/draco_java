package com.essenza.draco.modules.inventory.application.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record SupplierProductDto(
        Long id,
        Long supplierId,
        Long productId,
        Long skuId,
        String skuCode,
        String productName,
        String skuName,
        @Schema(description = "Precio de venta vigente del SKU") BigDecimal salePrice,
        BigDecimal unitCost,
        @Schema(description = "Margen bruto sobre el precio de venta (%)") BigDecimal marginPercent,
        String supplierCode,
        Integer leadTimeDays,
        @Schema(description = "Días de entrega efectivos (del producto o, si no, del proveedor)") Integer effectiveLeadTimeDays,
        int minOrderQty,
        boolean preferred,
        boolean active) {
}
