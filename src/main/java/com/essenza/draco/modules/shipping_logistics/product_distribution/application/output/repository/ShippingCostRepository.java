package com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.ShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.CreateShippingCostDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.shipping_cost.UpdateShippingCostDto;

import java.util.List;
import java.util.Optional;

public interface ShippingCostRepository {

    ShippingCostDto create(CreateShippingCostDto input);

    ShippingCostDto update(Long id, UpdateShippingCostDto input);

    boolean deleteById(Long id);

    Optional<ShippingCostDto> findById(Long id);

    List<ShippingCostDto> findAll();
}
