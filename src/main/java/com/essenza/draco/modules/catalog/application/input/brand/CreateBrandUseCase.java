package com.essenza.draco.modules.catalog.application.input.brand;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.dto.brand.CreateBrandDto;

public interface CreateBrandUseCase {
    BrandDto create(CreateBrandDto input);
}
