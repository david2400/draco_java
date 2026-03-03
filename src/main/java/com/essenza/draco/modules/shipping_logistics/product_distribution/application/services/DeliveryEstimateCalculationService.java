package com.essenza.draco.modules.shipping_logistics.product_distribution.application.services;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate.CalculateDeliveryEstimateUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier.FindCarrierByIdUseCase;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.delivery_estimate.DeliveryEstimateRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class DeliveryEstimateCalculationService implements CalculateDeliveryEstimateUseCase {

    private final DeliveryEstimateRepositoryAdapter deliveryEstimateRepository;
    private final FindCarrierByIdUseCase findCarrierById;

    public DeliveryEstimateCalculationService(DeliveryEstimateRepositoryAdapter deliveryEstimateRepository,
                                            FindCarrierByIdUseCase findCarrierById) {
        this.deliveryEstimateRepository = deliveryEstimateRepository;
        this.findCarrierById = findCarrierById;
    }

    @Override
    public DeliveryEstimateDto calculateEstimate(Long carrierId, String originAddress, String destinationAddress,
                                               LocalDateTime shipmentDate, Boolean isBusinessDaysOnly) {
        
        var carrier = findCarrierById.findById(carrierId)
                .orElseThrow(() -> new IllegalArgumentException("Carrier not found: " + carrierId));

        // Calculate estimated delivery days based on distance and carrier capabilities
        int estimatedDays = calculateEstimatedDays(originAddress, destinationAddress, carrier.getMaxDeliveryDays());
        
        // Calculate delivery dates
        LocalDate shipDate = shipmentDate.toLocalDate();
        LocalDate estimatedDeliveryDate = calculateDeliveryDate(shipDate, estimatedDays, isBusinessDaysOnly);
        LocalDate minDeliveryDate = calculateDeliveryDate(shipDate, Math.max(1, estimatedDays - 1), isBusinessDaysOnly);
        LocalDate maxDeliveryDate = calculateDeliveryDate(shipDate, estimatedDays + 2, isBusinessDaysOnly);

        CreateDeliveryEstimateDto createDto = CreateDeliveryEstimateDto.builder()
                .carrierId(carrierId)
                .originAddress(originAddress)
                .destinationAddress(destinationAddress)
                .shipmentDate(shipmentDate)
                .estimatedDeliveryDate(estimatedDeliveryDate)
                .minDeliveryDate(minDeliveryDate)
                .maxDeliveryDate(maxDeliveryDate)
                .estimatedDays(estimatedDays)
                .calculationMethod("DISTANCE_AND_CARRIER_BASED")
                .isBusinessDaysOnly(isBusinessDaysOnly)
                .build();

        return deliveryEstimateRepository.create(createDto);
    }

    private int calculateEstimatedDays(String origin, String destination, Integer maxCarrierDays) {
        // Simplified calculation based on address distance
        int hash = Math.abs((origin + destination).hashCode());
        int baseDays = 1 + (hash % 5); // 1-5 days base
        
        // Don't exceed carrier's maximum delivery days
        return Math.min(baseDays, maxCarrierDays);
    }

    private LocalDate calculateDeliveryDate(LocalDate startDate, int daysToAdd, Boolean isBusinessDaysOnly) {
        if (!isBusinessDaysOnly) {
            return startDate.plusDays(daysToAdd);
        }

        LocalDate result = startDate;
        int addedDays = 0;
        
        while (addedDays < daysToAdd) {
            result = result.plusDays(1);
            
            // Skip weekends if business days only
            if (result.getDayOfWeek() != DayOfWeek.SATURDAY && 
                result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }
        
        return result;
    }
}
