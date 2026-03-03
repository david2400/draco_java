package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.ShippingEstimateDto;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Caso de uso para obtener estimaciones de envío
 */
public interface GetShippingEstimateUseCase {
    
    /**
     * Obtiene una estimación completa de envío que incluye costo y fecha de entrega
     *
     * @param carrierCode código de la transportadora
     * @param originZip código postal de origen
     * @param destinationZip código postal de destino
     * @param weight peso del envío
     * @param serviceType tipo de servicio (express, standard, etc.)
     * @return estimación de envío
     */
    CompletableFuture<ShippingEstimateDto> getShippingEstimate(
            String carrierCode,
            String originZip,
            String destinationZip,
            BigDecimal weight,
            String serviceType);
}
