package com.essenza.draco.modules.inventory.application.input.product;

import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindProductsUseCase {
    Page<ProductDto> findAll(Pageable pageable);
}
