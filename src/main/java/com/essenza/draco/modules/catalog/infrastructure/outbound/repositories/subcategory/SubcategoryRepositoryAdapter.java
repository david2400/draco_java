package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory;

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
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.application.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.SubcategoryMapper;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.SubcategoryEntity;

@Repository
public class SubcategoryRepositoryAdapter implements SubcategoryRepository {

    private final JpaSubcategoryRepository jpa;
    private final SubcategoryMapper mapper;

    public SubcategoryRepositoryAdapter(JpaSubcategoryRepository jpa, SubcategoryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public SubcategoryDto create(CreateSubcategoryDto input) {
        SubcategoryEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public SubcategoryDto update(Long id, UpdateSubcategoryDto input) {
        SubcategoryEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Subcategoría no encontrada: " + id));
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
    public Optional<SubcategoryDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<SubcategoryDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<SubcategoryDto> findByName(String name) {
        return jpa.findByName(name).map(mapper::toDto);
    }

    @Override
    public Page<SubcategoryDto> search(String query, Long categoryId, Pageable pageable) {
        Specification<SubcategoryEntity> spec = (root, criteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isBlank()) {
                String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), like),
                        cb.like(cb.lower(root.<String>get("slug")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("description"), "")), like)));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
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

    public long countByCategoryId(Long categoryId) {
        return jpa.countByCategoryId(categoryId);
    }
}
