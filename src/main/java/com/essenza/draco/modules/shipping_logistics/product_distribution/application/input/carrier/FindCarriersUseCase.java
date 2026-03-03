package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;

import java.util.List;

public interface FindCarriersUseCase {
    List<CarrierDto> findAll();
}
