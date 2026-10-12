package com.essenza.draco.modules.catalog.application.services.product_child;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.input.product_child.FindProductChildrenUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindProductChildrenService implements FindProductChildrenUseCase {

    private final ProductChildRepository productChildRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductChildDto> findAll() {
        return productChildRepository.findAll();
    }
}
