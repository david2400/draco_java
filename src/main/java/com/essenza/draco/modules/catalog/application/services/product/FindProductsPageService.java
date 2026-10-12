package com.essenza.draco.modules.catalog.application.services.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductFilter;
import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductsPageService implements FindProductsPageUseCase {

    private final ProductRepository productRepository;
    private final ProductSoporte soporte;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPage(Pageable pageable, ProductFilter filter) {
        return soporte.hydratePage(productRepository.findAll(filter, pageable));
    }
}
