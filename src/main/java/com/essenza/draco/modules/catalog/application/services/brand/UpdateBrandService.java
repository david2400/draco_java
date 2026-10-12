package com.essenza.draco.modules.catalog.application.services.brand;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.application.input.brand.UpdateBrandUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;
import com.essenza.draco.shared.exceptions.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateBrandService implements UpdateBrandUseCase {

    private final BrandRepository repository;
    private final BrandSoporte soporte;

    @Override
    public BrandDto update(Long id, UpdateBrandDto input) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Marca no encontrada: " + id);
        }
        input.setId(id);
        soporte.normalize(input);
        soporte.ensureUnique(input.getName(), input.getSlug(), id);
        return repository.update(id, input);
    }
}
