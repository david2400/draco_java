package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.application.input.category.*;
import com.essenza.draco.modules.catalog.domain.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.CreateCategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.category.UpdateCategoryDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category.CategoryRepositoryAdapter;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryByIdUseCase, FindAllCategoriesUseCase, FindCategoryByIdUseCase {

    private final CategoryRepositoryAdapter categoryRepository;

    public CategoryServiceImpl(CategoryRepositoryAdapter categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public CategoryDto create(@Valid CreateCategoryDto input) {
        return categoryRepository.create(input);
    }

    @Override
    public CategoryDto update(Long id, @Valid UpdateCategoryDto input) {
        return categoryRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll();
    }
}
