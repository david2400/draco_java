package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;

import java.util.List;

public interface FindDispatchProductsUseCase {
    List<DispatchProductDto> findAll();
}
