package com.essenza.draco.modules.catalog.application.input.brand;

import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;

public interface UpdateBrandUseCase {
    BrandDto update(Long id, UpdateBrandDto input);
}
