package com.essenza.draco.modules.sales.domain.model.order;

/** Tipos de evento de la línea de tiempo de una orden. */
public enum OrderEventType {
    CREATED, STATUS, EDITED, LINES, DISCOUNT, PICKED, SHIPPED, DELIVERED, NOTE
}
