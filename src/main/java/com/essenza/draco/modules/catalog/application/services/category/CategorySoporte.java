package com.essenza.draco.modules.catalog.application.services.category;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.shared.common.web.Slugs;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Código compartido por los casos de uso de category (antes en CategoryServiceImpl). */
@Component
@RequiredArgsConstructor
class CategorySoporte {

    private final CategoryRepository repository;
    private final SubcategoryRepository subcategoryRepository;

    /** Recorta espacios y resuelve el slug (informado o derivado del nombre). */
    void normalize(CreateCategoryDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una categoría con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una categoría con el slug \"" + slug + "\".");
        }
    }

    boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Categoría no encontrada: " + id);
        }
        long children = subcategoryRepository.countByCategoryId(id);
        if (children > 0) {
            throw new ConflictException("No se puede eliminar la categoría: tiene " + children
                    + " subcategoría(s) asociada(s). Reasígnalas o elimínalas primero.");
        }
        return repository.deleteById(id);
    }
}
