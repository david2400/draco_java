package com.essenza.draco.modules.catalog.application.services.subcategory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.input.subcategory.UpdateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSubcategoryService implements UpdateSubcategoryUseCase {

    private final SubcategoryRepository repository;
    private final SubcategorySoporte soporte;

    @Override
    public SubcategoryDto update(Long id, UpdateSubcategoryDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Subcategoría no encontrada: " + id);
        }
        input.setId(id);
        soporte.normalize(input);
        soporte.ensureCategoryExists(input.getCategoryId());
        soporte.ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }
}
