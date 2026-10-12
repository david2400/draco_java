package com.essenza.draco.modules.catalog.application.services.product_child;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.catalog.application.input.product_child.UpdateProductChildUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProductChildService implements UpdateProductChildUseCase {

    private final ProductChildRepository productChildRepository;
    private final ProductChildSoporte soporte;

    @Override
    public ProductChildDto update(Long id, UpdateProductChildDto input) {
        soporte.checkNetContent(input);
        return productChildRepository.update(id, input);
    }
}
