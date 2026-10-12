package com.essenza.draco.modules.catalog.application.services.subcategory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.input.subcategory.CreateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSubcategoryService implements CreateSubcategoryUseCase {

    private final SubcategoryRepository repository;
    private final SubcategorySoporte soporte;

    @Override
    public SubcategoryDto create(CreateSubcategoryDto input) {
        soporte.normalize(input);
        soporte.ensureCategoryExists(input.getCategoryId());
        soporte.ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }
}
