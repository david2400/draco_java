package com.essenza.draco.modules.catalog.application.services.product_child;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.product_child.DeleteProductChildUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteProductChildService implements DeleteProductChildUseCase {

    private final ProductChildRepository productChildRepository;

    @Override
    public boolean deleteById(Long id) {
        return productChildRepository.deleteById(id);
    }
}
