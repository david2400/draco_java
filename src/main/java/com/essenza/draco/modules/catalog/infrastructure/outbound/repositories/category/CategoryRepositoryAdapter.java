package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category;

import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;
import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpa;
    private final CategoryMapper mapper;

    public CategoryRepositoryAdapter(JpaCategoryRepository jpa, CategoryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public CategoryDto create(CreateCategoryDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, UpdateCategoryDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
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
}
