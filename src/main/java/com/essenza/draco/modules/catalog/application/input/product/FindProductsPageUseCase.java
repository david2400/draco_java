package com.essenza.draco.modules.catalog.application.input.product;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindProductsPageUseCase {
    Page<ProductDto> findAllPage(Pageable pageable, ProductFilter filter);
}
