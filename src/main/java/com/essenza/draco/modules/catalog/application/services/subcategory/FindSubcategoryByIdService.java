package com.essenza.draco.modules.catalog.application.services.subcategory;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.application.input.subcategory.FindSubcategoryByIdUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.SubcategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindSubcategoryByIdService implements FindSubcategoryByIdUseCase {

    private final SubcategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<SubcategoryDto> findById(Long id) {
        return repository.findById(id);
    }
}
