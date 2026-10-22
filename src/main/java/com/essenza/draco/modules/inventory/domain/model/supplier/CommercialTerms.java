package com.essenza.draco.modules.inventory.domain.model.supplier;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Condiciones comerciales acordadas: plazo de pago (0 = contado), cupo de crédito, moneda,
 * tiempo de entrega, pedido mínimo y descuento.
 */
public record CommercialTerms(int paymentTermDays, BigDecimal creditLimit, String currency, Integer leadTimeDays,
                              BigDecimal minOrderAmount, BigDecimal discountPercent) {

    public static final CommercialTerms CASH = new CommercialTerms(0, null, "COP", null, null, null);

    public CommercialTerms {
        if (paymentTermDays < 0 || paymentTermDays > 365) {
            throw new SupplierRuleViolation("El plazo de pago va de 0 (contado) a 365 días.");
        }
        if (creditLimit != null && creditLimit.signum() < 0) {
            throw new SupplierRuleViolation("El cupo de crédito no puede ser negativo.");
        }
        currency = currency == null || currency.isBlank() ? "COP" : currency.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new SupplierRuleViolation("La moneda usa el código ISO de 3 letras (COP, USD, EUR…).");
        }
        if (leadTimeDays != null && (leadTimeDays < 0 || leadTimeDays > 365)) {
            throw new SupplierRuleViolation("El tiempo de entrega va de 0 a 365 días.");
        }
        if (minOrderAmount != null && minOrderAmount.signum() < 0) {
            throw new SupplierRuleViolation("El pedido mínimo no puede ser negativo.");
        }
        if (discountPercent != null && (discountPercent.signum() < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new SupplierRuleViolation("El descuento va de 0 a 100 %.");
        }
        if (paymentTermDays == 0 && creditLimit != null && creditLimit.signum() > 0) {
            throw new SupplierRuleViolation("Un proveedor de contado no tiene cupo de crédito: indica el plazo de pago.");
        }
    }

    public boolean credit() {
        return paymentTermDays > 0;
    }
}
