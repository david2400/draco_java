package com.essenza.draco.modules.catalog.application.services.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.application.input.category.CreateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryRepository repository;
    private final CategorySoporte soporte;

    @Override
    public CategoryDto create(CreateCategoryDto input) {
        soporte.normalize(input);
        soporte.ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }
}
