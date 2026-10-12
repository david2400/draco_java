package com.essenza.draco.modules.catalog.application.services.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductsService implements FindProductsUseCase {

    private final ProductRepository productRepository;
    private final ProductSoporte soporte;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        return soporte.hydratePage(productRepository.findAll(pageable));
    }
}
