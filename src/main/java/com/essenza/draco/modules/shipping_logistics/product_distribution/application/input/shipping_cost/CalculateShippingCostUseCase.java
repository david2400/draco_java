package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;

import java.math.BigDecimal;

public interface CalculateShippingCostUseCase {
    ShippingCostDto calculateCost(Long carrierId, String originAddress, String destinationAddress, 
                                 BigDecimal weight, BigDecimal volume);
}
