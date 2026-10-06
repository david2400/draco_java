package com.essenza.draco.modules.catalog.application.input.product_child;

import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;

import java.util.List;

public interface FindProductChildrenUseCase {
    List<ProductChildDto> findAll();
}
