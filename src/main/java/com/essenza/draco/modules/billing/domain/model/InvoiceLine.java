package com.essenza.draco.modules.billing.domain.model;

import java.math.BigDecimal;

/** Línea de factura: valores con impuesto incluido (total = base + impuesto). */
public record InvoiceLine(Long id, int position, Long orderLineId, String skuCode, String description, int quantity,
                          BigDecimal unitPrice, BigDecimal discount, String taxCode, String taxKind, BigDecimal taxRate,
                          BigDecimal taxBase, BigDecimal taxAmount, BigDecimal total) {

    public InvoiceLine {
        if (quantity <= 0) {
            throw new BillingRuleViolation("La línea \"" + description + "\" debe tener cantidad mayor que cero.");
        }
        if (taxBase.add(taxAmount).compareTo(total) != 0) {
            throw new BillingRuleViolation("La línea \"" + description + "\" no cuadra: base + impuesto ≠ total.");
        }
    }
}
