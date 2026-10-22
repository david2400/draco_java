package com.essenza.draco.modules.billing.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tarifa de impuesto. Código en mayúsculas (IVA_19), porcentaje entre 0 y 100 y,
 * si es excluida, porcentaje 0. La tarifa por defecto no se puede desactivar.
 */
public record TaxRate(Long id, String code, String name, String description, TaxKind kind, BigDecimal rate,
                      boolean isDefault, boolean active, int position) {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public TaxRate {
        if (code == null || !code.matches("[A-Z][A-Z0-9_]{1,29}")) {
            throw new BillingRuleViolation("El código de la tarifa usa mayúsculas, números y guion bajo (2 a 30 caracteres).");
        }
        if (name == null || name.isBlank()) {
            throw new BillingRuleViolation("El nombre de la tarifa es obligatorio.");
        }
        if (kind == null) {
            throw new BillingRuleViolation("Indica el tipo de impuesto.");
        }
        rate = rate == null ? BigDecimal.ZERO.setScale(3) : rate.setScale(3, RoundingMode.HALF_UP);
        if (rate.signum() < 0 || rate.compareTo(HUNDRED) > 0) {
            throw new BillingRuleViolation("El porcentaje debe estar entre 0 y 100.");
        }
        if (kind == TaxKind.EXCLUDED && rate.signum() != 0) {
            throw new BillingRuleViolation("Una tarifa excluida no causa impuesto: su porcentaje debe ser 0.");
        }
        if (isDefault && !active) {
            throw new BillingRuleViolation("La tarifa por defecto no se puede desactivar.");
        }
        name = name.trim();
    }
}
