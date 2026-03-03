package com.essenza.draco.modules.sales.application.input.product_order;

import com.essenza.draco.modules.sales.domain.dto.product_order.CreateProductOrderDto;
import com.essenza.draco.modules.sales.domain.dto.product_order.ProductOrderDto;

public interface CreateProductOrderUseCase {
    ProductOrderDto create(CreateProductOrderDto input);
}
