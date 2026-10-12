package com.essenza.draco.modules.catalog.application.services.product_combo;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.catalog.application.input.product_combo.FindProductComboByIdUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductComboRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductComboByIdService implements FindProductComboByIdUseCase {

    private final ProductComboRepository productComboRepository;

    @Transactional(readOnly = true)
    public Optional<ProductComboDto> findById(Long id) {
        return productComboRepository.findById(id);
    }
}
