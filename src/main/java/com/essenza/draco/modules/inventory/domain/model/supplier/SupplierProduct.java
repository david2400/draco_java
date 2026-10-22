package com.essenza.draco.modules.inventory.domain.model.supplier;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SKU que surte un proveedor, con su referencia, costo de compra, tiempo de entrega (si difiere
 * del general del proveedor), cantidad mínima y si es el proveedor preferido del SKU.
 */
public record SupplierProduct(Long id, Long supplierId, Long productId, Long skuId, String supplierCode,
                              BigDecimal unitCost, Integer leadTimeDays, int minOrderQty, boolean preferred,
                              boolean active) {

    public SupplierProduct {
        if (supplierId == null) {
            throw new SupplierRuleViolation("Falta el proveedor del producto.");
        }
        if (skuId == null) {
            throw new SupplierRuleViolation("Elige el producto (SKU) que surte el proveedor.");
        }
        supplierCode = Supplier.blank(supplierCode);
        if (supplierCode != null && supplierCode.length() > 64) {
            throw new SupplierRuleViolation("La referencia del proveedor tiene máximo 64 caracteres.");
        }
        if (unitCost == null || unitCost.signum() < 0) {
            throw new SupplierRuleViolation("Indica el costo de compra (0 o más).");
        }
        unitCost = unitCost.setScale(2, RoundingMode.HALF_UP);
        if (leadTimeDays != null && (leadTimeDays < 0 || leadTimeDays > 365)) {
            throw new SupplierRuleViolation("El tiempo de entrega va de 0 a 365 días.");
        }
        if (minOrderQty < 1) {
            throw new SupplierRuleViolation("La cantidad mínima de compra es 1 o más.");
        }
        if (preferred && !active) {
            throw new SupplierRuleViolation("Un producto inactivo no puede ser el preferido del SKU.");
        }
    }

    /** Margen bruto sobre el precio de venta (null si no hay precio). */
    public BigDecimal marginPercent(BigDecimal salePrice) {
        if (salePrice == null || salePrice.signum() <= 0) {
            return null;
        }
        return salePrice.subtract(unitCost).multiply(BigDecimal.valueOf(100)).divide(salePrice, 1, RoundingMode.HALF_UP);
    }
}
