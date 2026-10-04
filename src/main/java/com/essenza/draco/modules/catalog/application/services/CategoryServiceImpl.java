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
import com.essenza.draco.modules.catalog.application.input.category.BulkDeleteCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.CreateCategoryUseCase;
import com.essenza.draco.modules.catalog.application.input.category.DeleteCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindAllCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.FindCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.category.SearchCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.input.category.UpdateCategoryUseCase;
import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category.CategoryRepositoryAdapter;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory.SubcategoryRepositoryAdapter;

/**
 * Casos de uso de categoría.
 *
 * Reglas de negocio que antes no existían (y terminaban en errores 500):
 * nombre y slug únicos (409), slug generado desde el nombre si llega vacío,
 * 404 al actualizar/eliminar un id inexistente y no se elimina una categoría con subcategorías.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryByIdUseCase, FindAllCategoriesUseCase,
        FindCategoryByIdUseCase, SearchCategoriesUseCase, BulkDeleteCategoriesUseCase {

    private final CategoryRepositoryAdapter repository;
    private final SubcategoryRepositoryAdapter subcategoryRepository;

    public CategoryServiceImpl(CategoryRepositoryAdapter repository,
                               SubcategoryRepositoryAdapter subcategoryRepository) {
        this.repository = repository;
        this.subcategoryRepository = subcategoryRepository;
    }

    @Override
    public CategoryDto create(CreateCategoryDto input) {
        normalize(input);
        ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }

    @Override
    public CategoryDto update(Long id, UpdateCategoryDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Categoría no encontrada: " + id);
        }
        input.setId(id);
        normalize(input);
        ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
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

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryDto> search(String query, Pageable pageable) {
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
    private void normalize(CreateCategoryDto input) {
        input.setName(input.getName() == null ? null : input.getName().trim());
        input.setDescription(input.getDescription() == null || input.getDescription().isBlank()
                ? null
                : input.getDescription().trim());
        input.setSlug(Slugs.resolve(input.getSlug(), input.getName()));
    }

    private void ensureUnique(String name, String slug, Long excludeId) {
        if (repository.existsByName(name, excludeId)) {
            throw new ConflictException("Ya existe una categoría con el nombre \"" + name + "\".");
        }
        if (slug != null && !slug.isBlank() && repository.existsBySlug(slug, excludeId)) {
            throw new ConflictException("Ya existe una categoría con el slug \"" + slug + "\".");
        }
    }
}
