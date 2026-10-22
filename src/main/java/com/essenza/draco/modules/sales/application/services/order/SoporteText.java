package com.essenza.draco.modules.sales.application.services.order;

/** Limpieza de textos libres de la cabecera (vacío = null). */
final class SoporteText {

    private SoporteText() {
    }

    /** Una línea: espacios colapsados. Un texto más largo que {@code max} se rechaza. */
    static String clean(String value, int max) {
        if (value == null) {
            return null;
        }
        String text = value.trim().replaceAll("\\s+", " ");
        if (text.isEmpty()) {
            return null;
        }
        if (text.length() > max) {
            throw new com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation("Máximo " + max + " caracteres.");
        }
        return text;
    }

    /** Multilínea (notas): conserva los saltos de línea. */
    static String note(String value, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.strip();
        if (text.length() > max) {
            throw new com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation("Las notas tienen máximo " + max + " caracteres.");
        }
        return text;
    }
}
