package com.essenza.draco.modules.catalog.application.services.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.application.input.category.UpdateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryRepository repository;
    private final CategorySoporte soporte;

    @Override
    public CategoryDto update(Long id, UpdateCategoryDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Categoría no encontrada: " + id);
        }
        input.setId(id);
        soporte.normalize(input);
        soporte.ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }
}
