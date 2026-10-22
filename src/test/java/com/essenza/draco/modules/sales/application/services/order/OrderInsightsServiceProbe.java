package com.essenza.draco.modules.sales.application.services.order;

/** Acceso de prueba a los helpers package-private. */
public final class OrderInsightsServiceProbe {
    private OrderInsightsServiceProbe() {
    }

    public static String centralDocType(String value) {
        return OrderInsightsService.centralDocType(value);
    }

    public static String storeDocType(String value) {
        return OrderInsightsService.storeDocType(value);
    }
}
