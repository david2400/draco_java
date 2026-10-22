package com.essenza.draco.modules.sales.domain.model.order;

/** Cómo recibe el cliente la orden. */
public enum DeliveryMethod {
    /** Envío a una dirección (transportadora o reparto propio). */
    SHIPPING,
    /** Retiro en una tienda. */
    PICKUP;

    public static DeliveryMethod parse(String value) {
        return Enums.parse(DeliveryMethod.class, value, SHIPPING, "Método de entrega no válido: %s (SHIPPING o PICKUP).");
    }
}
