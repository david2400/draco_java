package com.essenza.draco.modules.catalog.application.services.product;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard;
import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;
import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.domain.services.ProductFactory;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductAggregateRepository aggregateRepository;
    private final ProductDtoAssembler productDtoAssembler;
    private final ProductCommandAssembler productCommandAssembler;
    private final ProductFactory productFactory;
    private final ProductPublicationGuard publicationGuard;
    private final ProductSoporte soporte;

    @Override
    public ProductDto update(Long id, @Valid UpdateProductDto input) {
        var current = aggregateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + id));
        soporte.checkNetContentUnit(input.getNetContentUnitId());
        soporte.checkTaxRate(input.getTaxRateId());
        var command = soporte.withUniqueSlug(productCommandAssembler.fromUpdateDto(id, input, current), id);
        var product = productFactory.fromCommand(command);
        if (product.getStatus() == ProductStatus.ACTIVE && current.getStatus() != ProductStatus.ACTIVE) {
            // Publicar exige la ficha técnica y los ejes de variante completos (Fase 4).
            publicationGuard.assertPublishable(id);
        }
        Long persistedId = productRepository.save(product);
        return aggregateRepository.findById(persistedId)
                .map(productDtoAssembler::toDto)
                .map(soporte::enrich)
                .orElseThrow(() -> new IllegalStateException("Product not found after update: " + persistedId));
    }
}
