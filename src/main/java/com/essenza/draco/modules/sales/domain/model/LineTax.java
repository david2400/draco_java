package com.essenza.draco.modules.sales.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Impuesto incluido en el total de una línea (los precios del catálogo incluyen IVA):
 * base = total / (1 + tarifa / 100) redondeada a 2 decimales; impuesto = total − base.
 * Una tarifa excluida o de 0 % deja todo el total como base.
 */
public record LineTax(Long rateId, String code, String kind, BigDecimal rate, BigDecimal base, BigDecimal amount) {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public static LineTax included(Long rateId, String code, String kind, BigDecimal rate, BigDecimal total) {
        BigDecimal gross = total == null ? BigDecimal.ZERO.setScale(2) : total.setScale(2, RoundingMode.HALF_UP);
        BigDecimal percent = rate == null ? BigDecimal.ZERO : rate.setScale(3, RoundingMode.HALF_UP);
        if (percent.signum() < 0 || percent.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException("La tarifa de impuesto debe estar entre 0 y 100.");
        }
        if ("EXCLUDED".equals(kind) || percent.signum() == 0) {
            return new LineTax(rateId, code, kind, percent, gross, BigDecimal.ZERO.setScale(2));
        }
        BigDecimal factor = BigDecimal.ONE.add(percent.divide(HUNDRED, 6, RoundingMode.HALF_UP));
        BigDecimal base = gross.divide(factor, 2, RoundingMode.HALF_UP);
        return new LineTax(rateId, code, kind, percent, base, gross.subtract(base));
    }

    /** Recalcula base e impuesto para otro total con la misma tarifa. */
    public LineTax forTotal(BigDecimal total) {
        return included(rateId, code, kind, rate, total);
    }
}
