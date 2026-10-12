package com.essenza.draco.modules.catalog.application.services.product;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;
import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;
import com.essenza.draco.modules.catalog.domain.services.ProductFactory;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductAggregateRepository aggregateRepository;
    private final ProductDtoAssembler productDtoAssembler;
    private final ProductCommandAssembler productCommandAssembler;
    private final ProductFactory productFactory;
    private final ProductSoporte soporte;

    @Override
    public ProductDto create(@Valid CreateProductDto input) {
        soporte.checkNetContentUnit(input.getNetContentUnitId());
        var command = soporte.withUniqueSlug(productCommandAssembler.fromCreateDto(input), null);
        var product = productFactory.fromCommand(command);
        Long id = productRepository.save(product);
        return aggregateRepository.findById(id)
                .map(productDtoAssembler::toDto)
                .map(soporte::enrich)
                .orElseThrow(() -> new IllegalStateException("Product not found after creation: " + id));
    }
}
