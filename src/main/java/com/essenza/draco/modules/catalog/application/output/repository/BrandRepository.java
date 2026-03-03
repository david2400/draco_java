package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;

import java.util.List;
import java.util.Optional;

public interface BrandRepository  {
 
    BrandDto create(CreateBrandDto input);

    BrandDto update(Long id, UpdateBrandDto input);

    boolean deleteById(Long id);

    Optional<BrandDto> findById(Long id);

    List<BrandDto> findAll();

    Optional<BrandDto> findByName(String name);
}
