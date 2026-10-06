package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.DispatchProductDto;

public interface CreateDispatchProductUseCase {
    DispatchProductDto create(CreateDispatchProductDto input);
}
