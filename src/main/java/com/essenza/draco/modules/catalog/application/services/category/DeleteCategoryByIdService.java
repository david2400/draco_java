package com.essenza.draco.modules.catalog.application.services.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.category.DeleteCategoryByIdUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteCategoryByIdService implements DeleteCategoryByIdUseCase {

    private final CategorySoporte soporte;

    @Override
    public boolean deleteById(Long id) {
        return soporte.deleteById(id);
    }
}
