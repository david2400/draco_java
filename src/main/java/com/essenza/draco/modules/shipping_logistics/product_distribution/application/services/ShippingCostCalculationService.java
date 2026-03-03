package com.essenza.draco.modules.shipping_logistics.product_distribution.application.services;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost.CalculateShippingCostUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarrierByIdUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.shipping_cost.ShippingCostRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class ShippingCostCalculationService implements CalculateShippingCostUseCase {

    private final ShippingCostRepositoryAdapter shippingCostRepository;
    private final FindCarrierByIdUseCase findCarrierById;
    private final WebScrapingShippingService webScrapingService;

    public ShippingCostCalculationService(ShippingCostRepositoryAdapter shippingCostRepository,
                                        FindCarrierByIdUseCase findCarrierById,
                                        WebScrapingShippingService webScrapingService) {
        this.shippingCostRepository = shippingCostRepository;
        this.findCarrierById = findCarrierById;
        this.webScrapingService = webScrapingService;
    }

    @Override
    public ShippingCostDto calculateCost(Long carrierId, String originAddress, String destinationAddress,
                                       BigDecimal weight, BigDecimal volume) {
        
        var carrier = findCarrierById.findById(carrierId)
                .orElseThrow(() -> new IllegalArgumentException("Carrier not found: " + carrierId));

        // Try to get rate from external web scraping first
        BigDecimal scrapedCost = null;
        String calculationMethod = "INTERNAL_CALCULATION";
        
        if (webScrapingService.isScrapingAvailable(carrier.getCode())) {
            scrapedCost = webScrapingService.scrapeShippingRate(carrier.getCode(), originAddress, 
                                                              destinationAddress, weight, volume);
            if (scrapedCost != null) {
                calculationMethod = "WEB_SCRAPING_" + carrier.getCode().toUpperCase();
            }
        }
        
        BigDecimal totalCost;
        BigDecimal estimatedDistance = calculateDistance(originAddress, destinationAddress);
        
        if (scrapedCost != null) {
            totalCost = scrapedCost;
        } else {
            // Fallback to internal calculation
            BigDecimal baseCost = carrier.getBaseRate();
            BigDecimal distanceCost = carrier.getRatePerKm().multiply(estimatedDistance);
            BigDecimal weightCost = calculateWeightCost(weight);
            BigDecimal volumeCost = calculateVolumeCost(volume);
            
            totalCost = baseCost.add(distanceCost).add(weightCost).add(volumeCost);
        }
        
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);

        CreateShippingCostDto createDto = CreateShippingCostDto.builder()
                .carrierId(carrierId)
                .originAddress(originAddress)
                .destinationAddress(destinationAddress)
                .distance(estimatedDistance)
                .weight(weight)
                .volume(volume)
                .calculatedCost(totalCost)
                .calculationMethod(calculationMethod)
                .isEstimated(true)
                .build();

        return shippingCostRepository.create(createDto);
    }

    private BigDecimal calculateDistance(String origin, String destination) {
        // Simplified distance calculation - in real implementation use Google Maps API or similar
        // For now, return a random distance between 10-500 km based on address hash
        int hash = Math.abs((origin + destination).hashCode());
        double distance = 10 + (hash % 490);
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeightCost(BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // $0.50 per kg
        return weight.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVolumeCost(BigDecimal volume) {
        if (volume == null || volume.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // $2.00 per cubic meter
        return volume.multiply(BigDecimal.valueOf(2.00)).setScale(2, RoundingMode.HALF_UP);
    }
}
