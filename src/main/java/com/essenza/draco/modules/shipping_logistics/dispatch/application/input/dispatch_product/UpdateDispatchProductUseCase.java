package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;

public interface UpdateDispatchProductUseCase {
    DispatchProductDto update(Long id, UpdateDispatchProductDto input);
}
