package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.dto.dispatch_product.DispatchProductDto;

import java.util.Optional;

public interface FindDispatchProductByIdUseCase {
    Optional<DispatchProductDto> findById(Long id);
}
