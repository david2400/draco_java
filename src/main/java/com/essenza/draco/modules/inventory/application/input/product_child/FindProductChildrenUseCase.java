package com.essenza.draco.modules.inventory.application.input.product_child;

import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;

import java.util.List;

public interface FindProductChildrenUseCase {
    List<ProductChildDto> findAll();
}
