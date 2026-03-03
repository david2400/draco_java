package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.shipping_cost;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;

import java.util.Optional;

public interface FindShippingCostByIdUseCase {
    Optional<ShippingCostDto> findById(Long id);
}
