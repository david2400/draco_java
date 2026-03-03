package com.essenza.draco.modules.inventory.application.input.product;

import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindProductsPageUseCase {
    Page<ProductDto> findAllPage(Pageable pageable, ProductFilter filter);
}
