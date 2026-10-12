package com.essenza.draco.modules.catalog.application.services.brand;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;
import com.essenza.draco.shared.common.web.Slugs;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;

/** Código compartido por los casos de uso de brand (antes en BrandServiceImpl). */
@Component
@RequiredArgsConstructor
class BrandSoporte {

    private final BrandRepository repository;

    /** Recorta espacios y resuelve el slug (informado o derivado del nombre). */
    void normalize(CreateBrandDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una marca con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una marca con el slug \"" + slug + "\".");
        }
    }

    boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Marca no encontrada: " + id);
        }
        return repository.deleteById(id);
    }
}
