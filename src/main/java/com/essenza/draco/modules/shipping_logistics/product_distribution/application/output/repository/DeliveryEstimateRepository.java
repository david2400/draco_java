package com.essenza.draco.modules.shipping_logistics.product_distribution.application.output.repository;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.DeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.CreateDeliveryEstimateDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.delivery_estimate.UpdateDeliveryEstimateDto;

import java.util.List;
import java.util.Optional;

public interface DeliveryEstimateRepository {

    DeliveryEstimateDto create(CreateDeliveryEstimateDto input);

    DeliveryEstimateDto update(Long id, UpdateDeliveryEstimateDto input);

    boolean deleteById(Long id);

    Optional<DeliveryEstimateDto> findById(Long id);

    List<DeliveryEstimateDto> findAll();
}
