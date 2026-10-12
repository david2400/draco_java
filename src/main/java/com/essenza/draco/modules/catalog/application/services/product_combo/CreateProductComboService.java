package com.essenza.draco.modules.catalog.application.services.product_combo;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.catalog.application.input.product_combo.CreateProductComboUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductComboRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateProductComboService implements CreateProductComboUseCase {

    private final ProductComboRepository productComboRepository;

    @Override
    public ProductComboDto create(@Valid CreateProductComboDto input) {
        return productComboRepository.create(input);
    }
}
