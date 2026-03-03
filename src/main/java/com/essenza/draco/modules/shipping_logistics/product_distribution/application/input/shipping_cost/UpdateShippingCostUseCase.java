package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;

public interface UpdateShippingCostUseCase {
    ShippingCostDto update(Long id, UpdateShippingCostDto input);
}
