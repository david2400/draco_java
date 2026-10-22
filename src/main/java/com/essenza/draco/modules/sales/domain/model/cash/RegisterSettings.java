package com.essenza.draco.modules.sales.domain.model.cash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Reglas de una caja: medios de pago permitidos (vacío = todos), base de apertura, tope de
 * efectivo, arqueo ciego, tolerancia de diferencia al cerrar, descuento máximo por línea,
 * factura automática, ancho del ticket y equipo.
 */
public record RegisterSettings(
        List<Long> allowedMethodIds,
        OpeningMode openingMode,
        BigDecimal defaultOpeningAmount,
        BigDecimal cashLimit,
        boolean blindClose,
        BigDecimal differenceTolerance,
        BigDecimal maxDiscountPercent,
        boolean autoInvoice,
        String receiptWidth,
        String deviceName) {

    public static final Set<String> RECEIPT_WIDTHS = Set.of("58MM", "80MM");

    public RegisterSettings {
        allowedMethodIds = allowedMethodIds == null ? List.of() : allowedMethodIds.stream().distinct().sorted().toList();
        openingMode = openingMode == null ? OpeningMode.FREE : openingMode;
        defaultOpeningAmount = money(defaultOpeningAmount);
        cashLimit = money(cashLimit);
        differenceTolerance = differenceTolerance == null ? BigDecimal.ZERO.setScale(2) : money(differenceTolerance);
        maxDiscountPercent = maxDiscountPercent == null ? null : maxDiscountPercent.setScale(2, RoundingMode.HALF_UP);
        receiptWidth = receiptWidth == null || receiptWidth.isBlank() ? "80MM" : receiptWidth.trim().toUpperCase(Locale.ROOT);
        deviceName = deviceName == null || deviceName.isBlank() ? null : deviceName.trim();
        if (openingMode != OpeningMode.FREE && (defaultOpeningAmount == null || defaultOpeningAmount.signum() < 0)) {
            throw new CashRuleViolation("Indica la base de apertura (sugerida o fija) en 0 o más.");
        }
        if (defaultOpeningAmount != null && defaultOpeningAmount.signum() < 0) {
            throw new CashRuleViolation("La base de apertura no puede ser negativa.");
        }
        if (cashLimit != null && cashLimit.signum() <= 0) {
            throw new CashRuleViolation("El tope de efectivo debe ser mayor que cero (o vacío para no usarlo).");
        }
        if (cashLimit != null && defaultOpeningAmount != null && cashLimit.compareTo(defaultOpeningAmount) <= 0) {
            throw new CashRuleViolation("El tope de efectivo debe ser mayor que la base de apertura.");
        }
        if (differenceTolerance.signum() < 0) {
            throw new CashRuleViolation("La tolerancia de diferencia no puede ser negativa.");
        }
        if (maxDiscountPercent != null && (maxDiscountPercent.signum() < 0 || maxDiscountPercent.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new CashRuleViolation("El descuento máximo va de 0 a 100 %.");
        }
        if (!RECEIPT_WIDTHS.contains(receiptWidth)) {
            throw new CashRuleViolation("Ancho de ticket no válido: " + receiptWidth + " (58MM u 80MM).");
        }
        if (deviceName != null && deviceName.length() > 80) {
            throw new CashRuleViolation("El nombre del equipo admite máximo 80 caracteres.");
        }
    }

    public static RegisterSettings defaults() {
        return new RegisterSettings(List.of(), OpeningMode.FREE, null, null, false, BigDecimal.ZERO, null, false, "80MM", null);
    }

    /** El medio de pago se puede usar en esta caja. */
    public boolean allows(Long paymentMethodId) {
        return allowedMethodIds.isEmpty() || allowedMethodIds.contains(paymentMethodId);
    }

    /** Base del turno según el modo: fija obliga el valor; sugerida/libre lo propone si no llega. */
    public BigDecimal openingAmount(BigDecimal requested) {
        BigDecimal value = requested == null ? null : requested.setScale(2, RoundingMode.HALF_UP);
        if (openingMode == OpeningMode.FIXED) {
            if (value != null && value.compareTo(defaultOpeningAmount) != 0) {
                throw new CashRuleViolation("Esta caja abre siempre con una base fija de " + defaultOpeningAmount.toPlainString() + ".");
            }
            return defaultOpeningAmount;
        }
        if (value == null) {
            return defaultOpeningAmount == null ? BigDecimal.ZERO.setScale(2) : defaultOpeningAmount;
        }
        return value;
    }

    /** El efectivo esperado supera el tope: hay que hacer un retiro. */
    public boolean overCashLimit(BigDecimal expectedCash) {
        return cashLimit != null && expectedCash != null && expectedCash.compareTo(cashLimit) > 0;
    }

    /** La diferencia del arqueo supera la tolerancia (en valor absoluto). */
    public boolean exceedsTolerance(BigDecimal difference) {
        return difference != null && difference.abs().compareTo(differenceTolerance) > 0;
    }

    /** Descuento de una línea dentro del máximo permitido (porcentaje del subtotal). */
    public void checkDiscount(String label, BigDecimal subtotal, BigDecimal discount) {
        if (maxDiscountPercent == null || discount == null || discount.signum() <= 0 || subtotal == null || subtotal.signum() <= 0) {
            return;
        }
        BigDecimal max = subtotal.multiply(maxDiscountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (discount.compareTo(max) > 0) {
            throw new CashRuleViolation("\"" + label + "\": el descuento (" + discount.toPlainString() + ") supera el máximo de la caja ("
                    + maxDiscountPercent.stripTrailingZeros().toPlainString() + " % = " + max.toPlainString() + ").");
        }
    }

    private static BigDecimal money(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
