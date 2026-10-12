package com.essenza.draco.modules.catalog.application.services.subcategory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.subcategory.DeleteSubcategoryByIdUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSubcategoryByIdService implements DeleteSubcategoryByIdUseCase {

    private final SubcategorySoporte soporte;

    @Override
    public boolean deleteById(Long id) {
        return soporte.deleteById(id);
    }
}
