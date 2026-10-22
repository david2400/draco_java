package com.essenza.draco.modules.sales.domain.model.order;

/** Canal por el que entró la orden. */
public enum OrderChannel {
    BACKOFFICE, POS, ONLINE, PHONE, WHATSAPP, MARKETPLACE;

    public static OrderChannel parse(String value) {
        return Enums.parse(OrderChannel.class, value, BACKOFFICE,
                "Canal no válido: %s (BACKOFFICE, POS, ONLINE, PHONE, WHATSAPP o MARKETPLACE).");
    }
}
