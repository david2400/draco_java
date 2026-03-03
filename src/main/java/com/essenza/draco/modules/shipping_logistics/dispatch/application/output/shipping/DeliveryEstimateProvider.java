package com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Puerto de salida para obtener estimaciones de entrega
 */
public interface DeliveryEstimateProvider {
    
    /**
     * Obtiene fechas estimadas de entrega para diferentes tipos de servicio
     *
     * @param carrierCode código de la transportadora (FEDEX, UPS, DHL, etc.)
     * @param originZip código postal de origen
     * @param destinationZip código postal de destino
     * @return mapa con los tipos de servicio como clave y las fechas estimadas como valor
     */
    CompletableFuture<Map<String, LocalDate>> getDeliveryEstimates(String carrierCode, 
                                                               String originZip, 
                                                               String destinationZip);
    
    /**
     * Obtiene la fecha estimada de entrega para un tipo de servicio específico
     *
     * @param carrierCode código de la transportadora
     * @param originZip código postal de origen
     * @param destinationZip código postal de destino
     * @param serviceType tipo de servicio (express, standard, etc.)
     * @return fecha estimada de entrega
     */
    CompletableFuture<LocalDate> getDeliveryEstimateForService(String carrierCode, 
                                                          String originZip, 
                                                          String destinationZip,
                                                          String serviceType);
}
