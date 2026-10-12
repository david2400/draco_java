package com.essenza.draco.modules.catalog.application.services.product_combo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.product_combo.DeleteProductComboUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductComboRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteProductComboService implements DeleteProductComboUseCase {

    private final ProductComboRepository productComboRepository;

    public boolean deleteById(Long id) {
        return productComboRepository.deleteById(id);
    }
}
