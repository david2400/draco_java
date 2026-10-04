package com.essenza.draco.modules.catalog.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.Slugs;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.catalog.application.input.brand.BulkDeleteBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.CreateBrandUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.DeleteBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindAllBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.FindBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.SearchBrandsUseCase;
import com.essenza.draco.modules.catalog.application.input.brand.UpdateBrandUseCase;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand.BrandRepositoryAdapter;

/**
 * Casos de uso de marca.
 *
 * Reglas de negocio que antes no existían (y terminaban en errores 500):
 * nombre y slug únicos (409), slug generado desde el nombre si llega vacío,
 * 404 al actualizar/eliminar un id inexistente.
 */
@Service
@Transactional
public class BrandServiceImpl implements CreateBrandUseCase, UpdateBrandUseCase, DeleteBrandByIdUseCase, FindAllBrandsUseCase,
        FindBrandByIdUseCase, SearchBrandsUseCase, BulkDeleteBrandsUseCase {

    private final BrandRepositoryAdapter repository;

    public BrandServiceImpl(BrandRepositoryAdapter repository) {
        this.repository = repository;
    }

    @Override
    public BrandDto create(CreateBrandDto input) {
        normalize(input);
        ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }

    @Override
    public BrandDto update(Long id, UpdateBrandDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Marca no encontrada: " + id);
        }
        input.setId(id);
        normalize(input);
        ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Marca no encontrada: " + id);
        }
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandDto> search(String query, Pageable pageable) {
        return PageResponse.from(repository.search(query, pageable));
    }

    @Override
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            try {
                deleteById(id);
                result.success();
            } catch (NotFoundException | ConflictException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
    }

    /** Recorta espacios y resuelve el slug (informado o derivado del nombre). */
    private void normalize(CreateBrandDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    private void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una marca con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una marca con el slug \"" + slug + "\".");
        }
    }
}
