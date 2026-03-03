package com.essenza.draco.modules.shipping_logistics.product_distribution.application.input.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CreateCarrierDto;
import com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier.CarrierDto;

public interface CreateCarrierUseCase {
    CarrierDto create(CreateCarrierDto input);
}
