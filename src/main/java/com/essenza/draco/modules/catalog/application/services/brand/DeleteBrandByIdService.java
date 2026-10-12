package com.essenza.draco.modules.catalog.application.services.brand;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.brand.DeleteBrandByIdUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteBrandByIdService implements DeleteBrandByIdUseCase {

    private final BrandSoporte soporte;

    @Override
    public boolean deleteById(Long id) {
        return soporte.deleteById(id);
    }
}
