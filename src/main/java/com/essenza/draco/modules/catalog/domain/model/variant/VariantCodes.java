package com.essenza.draco.modules.catalog.domain.model.variant;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Códigos de SKU de variantes: base del producto más una abreviatura por valor (SKU-000003-ROJ-M). */
public final class VariantCodes {

    public static final Pattern SKU_CODE = Pattern.compile("[A-Z0-9][A-Z0-9._-]{1,63}");
    public static final Pattern BARCODE = Pattern.compile("[0-9A-Za-z-]{4,32}");

    private VariantCodes() {
    }

    /** Sin tildes, mayúsculas, solo letras y números. */
    public static String clean(String value) {
        if (value == null) {
            return "";
        }
        String plain = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return plain.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }

    /**
     * Abreviatura de un valor: cortos o con números se usan completos (M, XL, 50ML); el resto, las
     * tres primeras letras (ROJ). Máximo 8 caracteres.
     */
    public static String abbreviation(String value) {
        String c = clean(value);
        if (c.isEmpty()) {
            return "X";
        }
        if (c.length() <= 4 || c.chars().anyMatch(Character::isDigit)) {
            return c.length() > 8 ? c.substring(0, 8) : c;
        }
        return c.substring(0, 3);
    }

    public static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", "-");
    }

    public static boolean validCode(String code) {
        return code != null && SKU_CODE.matcher(code).matches();
    }

    public static boolean validBarcode(String barcode) {
        return barcode != null && BARCODE.matcher(barcode).matches();
    }
}
