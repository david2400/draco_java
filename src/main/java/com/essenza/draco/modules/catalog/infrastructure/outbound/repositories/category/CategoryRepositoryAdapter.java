package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.essenza.draco.shared.exceptions.NotFoundException;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.application.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.CategoryMapper;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.CategoryEntity;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpa;
    private final CategoryMapper mapper;

    public CategoryRepositoryAdapter(JpaCategoryRepository jpa, CategoryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto create(CreateCategoryDto input) {
        CategoryEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, UpdateCategoryDto input) {
        CategoryEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + id));
        mapper.updateEntityFromDto(input, entity);
        return mapper.toDto(jpa.save(entity));
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id); // @SoftDelete: marca deleted = 1
        return true;
    }

    @Override
    public Optional<CategoryDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<CategoryDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<CategoryDto> findByName(String name) {
        return jpa.findByName(name).map(mapper::toDto);
    }

    @Override
    public Page<CategoryDto> search(String query, Pageable pageable) {
        Specification<CategoryEntity> spec = (root, criteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), like),
                        cb.like(cb.lower(root.<String>get("slug")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("description"), "")), like)));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return jpa.findAll(spec, pageable).map(mapper::toDto);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        return excludeId == null
                ? jpa.existsByNameIgnoreCase(name)
                : jpa.existsByNameIgnoreCaseAndIdNot(name, excludeId);
    }

    @Override
    public boolean existsBySlug(String slug, Long excludeId) {
        return excludeId == null
                ? jpa.existsBySlugIgnoreCase(slug)
                : jpa.existsBySlugIgnoreCaseAndIdNot(slug, excludeId);
    }

    public long count() {
        return jpa.count();
    }
}
