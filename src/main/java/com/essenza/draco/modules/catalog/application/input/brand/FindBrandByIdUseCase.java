package com.essenza.draco.modules.catalog.application.input.brand;

import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;

import java.util.Optional;

public interface FindBrandByIdUseCase {
    Optional<BrandDto> findById(Long id);
}
