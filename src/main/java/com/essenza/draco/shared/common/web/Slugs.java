package com.essenza.draco.shared.common.web;

import java.text.Normalizer;
import java.util.Locale;

/** Genera slugs URL-friendly ("Perfumes Árabes" → "perfumes-arabes"). */
public final class Slugs {

    private Slugs() {
    }

    public static String slugify(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s_]+", "-")
                .replaceAll("-+", "-");
        return normalized.replaceAll("^-|-$", "");
    }

    /** Slug informado (normalizado) o, si viene vacío, derivado del nombre. */
    public static String resolve(String slug, String fallbackName) {
        String candidate = slug != null && !slug.isBlank() ? slug : fallbackName;
        return slugify(candidate);
    }
}
