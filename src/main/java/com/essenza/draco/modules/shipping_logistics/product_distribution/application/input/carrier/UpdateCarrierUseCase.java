package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.UpdateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;

public interface UpdateCarrierUseCase {
    CarrierDto update(Long id, UpdateCarrierDto input);
}
