package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;

import java.util.Optional;

public interface FindDeliveryEstimateByIdUseCase {
    Optional<DeliveryEstimateDto> findById(Long id);
}
