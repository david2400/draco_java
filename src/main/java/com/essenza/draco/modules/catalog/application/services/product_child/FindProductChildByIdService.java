package com.essenza.draco.modules.catalog.application.services.product_child;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.input.product_child.FindProductChildByIdUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductChildByIdService implements FindProductChildByIdUseCase {

    private final ProductChildRepository productChildRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductChildDto> findById(Long id) {
        return productChildRepository.findById(id);
    }
}
