package com.essenza.draco.modules.catalog.application.input.product;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindProductsUseCase {
    Page<ProductDto> findAll(Pageable pageable);
}
