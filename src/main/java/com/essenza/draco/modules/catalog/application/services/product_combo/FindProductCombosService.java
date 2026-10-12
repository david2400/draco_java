package com.essenza.draco.modules.catalog.application.services.product_combo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.catalog.application.input.product_combo.FindProductCombosUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductComboRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductCombosService implements FindProductCombosUseCase {

    private final ProductComboRepository productComboRepository;

    @Transactional(readOnly = true)
    public List<ProductComboDto> findAll() {
        return productComboRepository.findAll();
    }
}
