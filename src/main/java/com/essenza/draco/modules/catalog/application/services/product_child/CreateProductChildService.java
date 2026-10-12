package com.essenza.draco.modules.catalog.application.services.product_child;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.input.product_child.CreateProductChildUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductChildService implements CreateProductChildUseCase {

    private final ProductChildRepository productChildRepository;
    private final ProductChildSoporte soporte;

    @Override
    public ProductChildDto create(CreateProductChildDto input) {
        soporte.checkNetContent(input);
        return productChildRepository.create(input);
    }
}
