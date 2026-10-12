package com.essenza.draco.modules.sales.domain.model.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * Medio de pago configurable: canal (físico/virtual), tipo, pasarela, comisión, límites y
 * reglas de captura (referencia obligatoria, cambio en efectivo).
 */
public record PaymentMethod(
        Long id,
        String code,
        String name,
        String description,
        String instructions,
        PaymentChannel channel,
        PaymentMethodKind kind,
        GatewayProvider provider,
        boolean active,
        int position,
        BigDecimal feePercent,
        BigDecimal feeFixed,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        boolean requiresReference,
        boolean allowsChange) {

    private static final Pattern CODE = Pattern.compile("^[A-Z][A-Z0-9_]{1,39}$");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public PaymentMethod {
        code = code == null ? null : code.trim().toUpperCase();
        name = name == null ? null : name.trim();
        description = blankToNull(description);
        instructions = blankToNull(instructions);
        feePercent = feePercent == null ? BigDecimal.ZERO : feePercent;
        feeFixed = feeFixed == null ? BigDecimal.ZERO : feeFixed;
        if (code == null || !CODE.matcher(code).matches()) {
            throw new PaymentRuleViolation("El código debe empezar por letra y usar solo MAYÚSCULAS, números y _ (2 a 40).");
        }
        if (name == null || name.isEmpty() || name.length() > 120) {
            throw new PaymentRuleViolation("El nombre es obligatorio (máximo 120 caracteres).");
        }
        if (channel == null || kind == null) {
            throw new PaymentRuleViolation("El canal y el tipo son obligatorios.");
        }
        if (!kind.allows(channel)) {
            throw new PaymentRuleViolation("Un medio de tipo " + kind + " no puede ser de canal " + channel + ".");
        }
        if (kind == PaymentMethodKind.GATEWAY && provider == null) {
            throw new PaymentRuleViolation("Una pasarela necesita proveedor (SANDBOX, WOMPI o MERCADO_PAGO).");
        }
        if (kind != PaymentMethodKind.GATEWAY) {
            provider = null;
        }
        if (allowsChange && kind != PaymentMethodKind.CASH && kind != PaymentMethodKind.CASH_ON_DELIVERY) {
            throw new PaymentRuleViolation("Solo el efectivo y la contraentrega admiten cambio.");
        }
        if (feePercent.signum() < 0 || feePercent.compareTo(HUNDRED) > 0) {
            throw new PaymentRuleViolation("La comisión porcentual debe estar entre 0 y 100.");
        }
        if (feeFixed.signum() < 0) {
            throw new PaymentRuleViolation("La comisión fija no puede ser negativa.");
        }
        if (minAmount != null && minAmount.signum() < 0 || maxAmount != null && maxAmount.signum() <= 0) {
            throw new PaymentRuleViolation("Los montos mínimo y máximo deben ser positivos.");
        }
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new PaymentRuleViolation("El monto mínimo no puede ser mayor que el máximo.");
        }
    }

    public boolean isGateway() {
        return kind == PaymentMethodKind.GATEWAY;
    }

    /** Comisión estimada del medio para {@code amount} (porcentaje + fija), a 2 decimales. */
    public BigDecimal feeFor(BigDecimal amount) {
        BigDecimal percent = amount.multiply(feePercent).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        return percent.add(feeFixed).setScale(2, RoundingMode.HALF_UP);
    }

    /** Valida que el monto esté dentro de los límites del medio. */
    public void checkAmount(BigDecimal amount) {
        if (minAmount != null && amount.compareTo(minAmount) < 0) {
            throw new PaymentRuleViolation("\"" + name + "\" acepta pagos desde " + minAmount.toPlainString() + ".");
        }
        if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
            throw new PaymentRuleViolation("\"" + name + "\" acepta pagos hasta " + maxAmount.toPlainString() + ".");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
