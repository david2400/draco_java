package com.essenza.draco.modules.sales.domain.model.order;

final class Texts {

    private Texts() {
    }

    static String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().replaceAll("\\s+", " ");
        return trimmed.isEmpty() ? null : trimmed;
    }

    static String clean(String value, int max, String field) {
        String text = clean(value);
        if (text != null && text.length() > max) {
            throw new OrderRuleViolation(field + ": máximo " + max + " caracteres.");
        }
        return text;
    }

    /** Texto multilínea (notas): conserva los saltos de línea. */
    static String note(String value, int max, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.strip();
        if (text.length() > max) {
            throw new OrderRuleViolation(field + ": máximo " + max + " caracteres.");
        }
        return text;
    }
}
