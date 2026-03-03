package com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Puerto de salida para consultar tarifas de envío
 */
public interface ShippingRateProvider {
    
    /**
     * Consulta el costo de envío para una transportadora específica
     *
     * @param carrierCode código de la transportadora (FEDEX, UPS, DHL, etc.)
     * @param originZip código postal de origen
     * @param destinationZip código postal de destino
     * @param weight peso en kg
     * @return costo de envío futuro
     */
    CompletableFuture<BigDecimal> getShippingRate(String carrierCode, String originZip, 
                                              String destinationZip, BigDecimal weight);
    
    /**
     * Verifica si el servicio de consulta está disponible para una transportadora
     * 
     * @param carrierCode código de la transportadora
     * @return verdadero si está disponible
     */
    boolean isServiceAvailable(String carrierCode);
}
