package com.essenza.draco.modules.catalog.application.services.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.category.CategoryDto;
import com.essenza.draco.modules.catalog.application.input.category.FindAllCategoriesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindAllCategoriesService implements FindAllCategoriesUseCase {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return repository.findAll();
    }
}
