package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.shipping;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping.DeliveryEstimateProvider;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.services.AdvancedWebScrapingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa DeliveryEstimateProvider utilizando el servicio AdvancedWebScrapingService
 */
@Component
public class WebScrapingEstimateAdapter implements DeliveryEstimateProvider {

    private final AdvancedWebScrapingService webScrapingService;
    
    public WebScrapingEstimateAdapter(AdvancedWebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }
    
    @Override
    public CompletableFuture<Map<String, LocalDate>> getDeliveryEstimates(String carrierCode, 
                                                               String originZip, 
                                                               String destinationZip) {
        return webScrapingService.scrapeDeliveryEstimate(carrierCode, originZip, destinationZip);
    }
    
    @Override
    public CompletableFuture<LocalDate> getDeliveryEstimateForService(String carrierCode, 
                                                          String originZip, 
                                                          String destinationZip,
                                                          String serviceType) {
        // Obtenemos todas las estimaciones y filtramos por tipo de servicio
        return webScrapingService.scrapeDeliveryEstimate(carrierCode, originZip, destinationZip)
                .thenApply(estimates -> estimates.getOrDefault(serviceType, null));
    }
}
