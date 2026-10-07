package com.essenza.draco.modules.catalog.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contenido neto del producto o variante (500 ml, 1 kg, 12 und). Valor y unidad van
 * juntos: ambos presentes o ambos ausentes.
 */
public record NetContent(BigDecimal value, Long unitId) {

    public NetContent {
        if (value == null || unitId == null) {
            throw new IllegalArgumentException("El contenido neto necesita valor y unidad.");
        }
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("El contenido neto debe ser mayor que 0.");
        }
        value = value.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
        if (value.scale() < 0) {
            value = value.setScale(0);
        }
    }

    /** {@code null} si no viene ninguno de los dos; error si solo viene uno. */
    public static NetContent of(BigDecimal value, Long unitId) {
        if (value == null && unitId == null) {
            return null;
        }
        return new NetContent(value, unitId);
    }

    public static BigDecimal valueOf(NetContent content) {
        return content == null ? null : content.value();
    }

    public static Long unitOf(NetContent content) {
        return content == null ? null : content.unitId();
    }
}
