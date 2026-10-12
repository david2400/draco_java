package com.essenza.draco.modules.catalog.application.services.category;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.input.category.FindCategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindCategoryByIdService implements FindCategoryByIdUseCase {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> findById(Long id) {
        return repository.findById(id);
    }
}
