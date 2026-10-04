package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand;

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
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.BrandMapper;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.BrandEntity;

@Repository
public class BrandRepositoryAdapter implements BrandRepository {

    private final JpaBrandRepository jpa;
    private final BrandMapper mapper;

    public BrandRepositoryAdapter(JpaBrandRepository jpa, BrandMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public BrandDto create(CreateBrandDto input) {
        BrandEntity saved = jpa.save(mapper.toEntity(input));
        return mapper.toDto(saved);
    }

    @Override
    public BrandDto update(Long id, UpdateBrandDto input) {
        BrandEntity entity = jpa.findById(id)
                .orElseThrow(() -> new NotFoundException("Marca no encontrada: " + id));
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
    public Optional<BrandDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<BrandDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<BrandDto> findByName(String name) {
        return jpa.findByName(name).map(mapper::toDto);
    }

    @Override
    public Page<BrandDto> search(String query, Pageable pageable) {
        Specification<BrandEntity> spec = (root, criteria, cb) -> {
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
