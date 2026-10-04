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
import com.essenza.draco.modules.catalog.application.input.subcategory.BulkDeleteSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.CreateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.DeleteSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindAllSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.SearchSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.subcategory.UpdateSubcategoryUseCase;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory.SubcategoryRepositoryAdapter;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category.CategoryRepositoryAdapter;

/**
 * Casos de uso de subcategoría.
 *
 * Reglas de negocio que antes no existían (y terminaban en errores 500):
 * nombre y slug únicos (409), slug generado desde el nombre si llega vacío,
 * 404 al actualizar/eliminar un id inexistente, la categoría padre debe existir.
 */
@Service
@Transactional
public class SubcategoryServiceImpl implements CreateSubcategoryUseCase, UpdateSubcategoryUseCase, DeleteSubcategoryByIdUseCase, FindAllSubcategoriesUseCase,
        FindSubcategoryByIdUseCase, SearchSubcategoriesUseCase, BulkDeleteSubcategoriesUseCase {

    private final SubcategoryRepositoryAdapter repository;
    private final CategoryRepositoryAdapter categoryRepository;

    public SubcategoryServiceImpl(SubcategoryRepositoryAdapter repository,
                                  CategoryRepositoryAdapter categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public SubcategoryDto create(CreateSubcategoryDto input) {
        normalize(input);
        ensureCategoryExists(input.getCategoryId());
        ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }

    @Override
    public SubcategoryDto update(Long id, UpdateSubcategoryDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Subcategoría no encontrada: " + id);
        }
        input.setId(id);
        normalize(input);
        ensureCategoryExists(input.getCategoryId());
        ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Subcategoría no encontrada: " + id);
        }
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubcategoryDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDto> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubcategoryDto> search(String query, Long categoryId, Pageable pageable) {
        return PageResponse.from(repository.search(query, categoryId, pageable));
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
    private void normalize(CreateSubcategoryDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    private void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una subcategoría con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una subcategoría con el slug \"" + slug + "\".");
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        if (categoryId == null || !categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("La categoría seleccionada no existe: " + categoryId);
        }
    }
}
