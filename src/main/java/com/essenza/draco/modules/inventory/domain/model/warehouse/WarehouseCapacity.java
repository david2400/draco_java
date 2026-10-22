package com.essenza.draco.modules.inventory.domain.model.warehouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Tamaño de la bodega: área en m², capacidad en unidades y en posiciones (todos opcionales). */
public record WarehouseCapacity(BigDecimal areaM2, Integer units, Integer positions) {

    public static final WarehouseCapacity NONE = new WarehouseCapacity(null, null, null);

    public WarehouseCapacity {
        if (areaM2 != null && areaM2.signum() <= 0) {
            throw new WarehouseRuleViolation("El área debe ser mayor que 0 m².");
        }
        if (units != null && units <= 0) {
            throw new WarehouseRuleViolation("La capacidad en unidades debe ser mayor que 0.");
        }
        if (positions != null && positions <= 0) {
            throw new WarehouseRuleViolation("La capacidad en posiciones debe ser mayor que 0.");
        }
        areaM2 = areaM2 == null ? null : areaM2.setScale(2, RoundingMode.HALF_UP);
    }

    /** Ocupación (%) con las unidades físicas actuales; null sin capacidad en unidades. */
    public BigDecimal occupancy(long onHand) {
        if (units == null) {
            return null;
        }
        return BigDecimal.valueOf(onHand * 100L).divide(BigDecimal.valueOf(units), 1, RoundingMode.HALF_UP);
    }
}
