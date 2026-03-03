package com.essenza.draco.modules.catalog.application.input.brand;

import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;

import java.util.List;

public interface FindAllBrandsUseCase {
    List<BrandDto> findAll();
}
