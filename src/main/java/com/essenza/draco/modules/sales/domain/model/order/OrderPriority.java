package com.essenza.draco.modules.sales.domain.model.order;

/** Prioridad de preparación. */
public enum OrderPriority {
    LOW, NORMAL, HIGH, URGENT;

    public static OrderPriority parse(String value) {
        return Enums.parse(OrderPriority.class, value, NORMAL, "Prioridad no válida: %s (LOW, NORMAL, HIGH o URGENT).");
    }
}
