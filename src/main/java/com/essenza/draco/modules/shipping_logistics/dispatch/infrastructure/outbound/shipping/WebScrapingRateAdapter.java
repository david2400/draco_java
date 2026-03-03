package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.shipping;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping.ShippingRateProvider;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.services.AdvancedWebScrapingService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa ShippingRateProvider utilizando el servicio AdvancedWebScrapingService
 */
@Component
public class WebScrapingRateAdapter implements ShippingRateProvider {

    private final AdvancedWebScrapingService webScrapingService;
    
    public WebScrapingRateAdapter(AdvancedWebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }
    
    @Override
    public CompletableFuture<BigDecimal> getShippingRate(String carrierCode, String originZip, 
                                              String destinationZip, BigDecimal weight) {
        return webScrapingService.scrapeRealShippingCost(carrierCode, originZip, destinationZip, weight);
    }
    
    @Override
    public boolean isServiceAvailable(String carrierCode) {
        return webScrapingService.isScrapingAvailable(carrierCode);
    }
}
