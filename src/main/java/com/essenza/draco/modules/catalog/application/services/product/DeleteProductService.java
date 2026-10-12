package com.essenza.draco.modules.catalog.application.services.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteProductService implements DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public boolean deleteById(Long id) {
        return productRepository.deleteById(id);
    }
}
