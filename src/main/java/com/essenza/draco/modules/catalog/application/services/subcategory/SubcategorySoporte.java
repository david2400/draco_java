package com.essenza.draco.modules.catalog.application.services.subcategory;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.shared.common.web.Slugs;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Código compartido por los casos de uso de subcategory (antes en SubcategoryServiceImpl). */
@Component
@RequiredArgsConstructor
class SubcategorySoporte {

    private final SubcategoryRepository repository;
    private final CategoryRepository categoryRepository;

    /** Recorta espacios y resuelve el slug (informado o derivado del nombre). */
    void normalize(CreateSubcategoryDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una subcategoría con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una subcategoría con el slug \"" + slug + "\".");
        }
    }

    void ensureCategoryExists(Long categoryId) {
        if (categoryId == null || !categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("La categoría seleccionada no existe: " + categoryId);
        }
    }

    boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Subcategoría no encontrada: " + id);
        }
        return repository.deleteById(id);
    }
}
