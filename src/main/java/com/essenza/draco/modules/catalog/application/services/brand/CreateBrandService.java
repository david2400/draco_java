package com.essenza.draco.modules.catalog.application.services.brand;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.application.input.brand.CreateBrandUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateBrandService implements CreateBrandUseCase {

    private final BrandRepository repository;
    private final BrandSoporte soporte;

    @Override
    public BrandDto create(CreateBrandDto input) {
        soporte.normalize(input);
        soporte.ensureUnique(input.getName(), input.getSlug(), null);
        return repository.create(input);
    }
}
