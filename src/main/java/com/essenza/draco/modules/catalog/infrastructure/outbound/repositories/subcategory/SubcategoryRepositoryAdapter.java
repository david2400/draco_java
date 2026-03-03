package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory;

import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.SubcategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class SubcategoryRepositoryAdapter implements SubcategoryRepository {

    private final JpaSubcategoryRepository jpa;
    private final SubcategoryMapper mapper;

    public SubcategoryRepositoryAdapter(JpaSubcategoryRepository jpa, SubcategoryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public SubcategoryDto create(CreateSubcategoryDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public SubcategoryDto update(Long id, UpdateSubcategoryDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subcategory not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
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
}
