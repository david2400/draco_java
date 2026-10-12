package com.essenza.draco.modules.catalog.application.services.subcategory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindAllSubcategoriesUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindAllSubcategoriesService implements FindAllSubcategoriesUseCase {

    private final SubcategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDto> findAll() {
        return repository.findAll();
    }
}
