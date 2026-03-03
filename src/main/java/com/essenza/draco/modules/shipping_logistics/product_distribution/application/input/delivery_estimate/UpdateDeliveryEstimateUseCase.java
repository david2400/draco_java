package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.delivery_estimate;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;

public interface UpdateDeliveryEstimateUseCase {
    DeliveryEstimateDto update(Long id, UpdateDeliveryEstimateDto input);
}
