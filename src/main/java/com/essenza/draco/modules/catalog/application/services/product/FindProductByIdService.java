package com.essenza.draco.modules.catalog.application.services.product;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductByIdService implements FindProductByIdUseCase {

    private final ProductAggregateRepository aggregateRepository;
    private final ProductDtoAssembler productDtoAssembler;
    private final ProductSoporte soporte;

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return aggregateRepository.findById(id).map(productDtoAssembler::toDto).map(soporte::enrich);
    }
}
