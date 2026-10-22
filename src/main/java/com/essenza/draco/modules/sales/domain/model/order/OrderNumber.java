package com.essenza.draco.modules.sales.domain.model.order;

import java.util.Locale;
import java.util.regex.Pattern;

/** Numeración de órdenes: PREFIJO-000123. */
public record OrderNumber(String prefix, int padding, long next) {

    private static final Pattern PREFIX = Pattern.compile("^[A-Z0-9]{1,10}$");

    public OrderNumber {
        prefix = prefix == null ? "" : prefix.trim().toUpperCase(Locale.ROOT);
        if (!PREFIX.matcher(prefix).matches()) {
            throw new OrderRuleViolation("El prefijo de las órdenes usa de 1 a 10 letras o números.");
        }
        if (padding < 1 || padding > 12) {
            throw new OrderRuleViolation("Los dígitos del número de orden van de 1 a 12.");
        }
        if (next < 1) {
            throw new OrderRuleViolation("El siguiente número de orden debe ser mayor que cero.");
        }
    }

    public String format(long number) {
        String digits = String.valueOf(number);
        if (digits.length() < padding) {
            digits = "0".repeat(padding - digits.length()) + digits;
        }
        return prefix + "-" + digits;
    }

    public String current() {
        return format(next);
    }

    public OrderNumber advance() {
        return new OrderNumber(prefix, padding, next + 1);
    }
}
