package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.domain.dto.subcategory.SubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.CreateSubcategoryDto;
import com.essenza.draco.modules.catalog.domain.dto.subcategory.UpdateSubcategoryDto;

import java.util.List;
import java.util.Optional;

public interface SubcategoryRepository {

    SubcategoryDto create(CreateSubcategoryDto input);

    SubcategoryDto update(Long id, UpdateSubcategoryDto input);

    boolean deleteById(Long id);

    Optional<SubcategoryDto> findById(Long id);

    List<SubcategoryDto> findAll();

    Optional<SubcategoryDto> findByName(String name);
}
