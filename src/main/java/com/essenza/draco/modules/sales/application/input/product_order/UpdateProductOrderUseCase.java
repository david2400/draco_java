package com.essenza.draco.modules.sales.application.input.product_order;

import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.UpdateProductOrderDto;

public interface UpdateProductOrderUseCase {
    ProductOrderDto update(Long id, UpdateProductOrderDto input);
}
