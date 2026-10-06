package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.application.dto.carrier.UpdateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.application.dto.carrier.CarrierDto;

public interface UpdateCarrierUseCase {
    CarrierDto update(Long id, UpdateCarrierDto input);
}
