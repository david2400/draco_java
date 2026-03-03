package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;

import java.time.LocalDateTime;

public interface CalculateDeliveryEstimateUseCase {
    DeliveryEstimateDto calculateEstimate(Long carrierId, String originAddress, String destinationAddress, 
                                        LocalDateTime shipmentDate, Boolean isBusinessDaysOnly);
}
