package com.essenza.draco.modules.catalog.application.services.brand;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.input.brand.FindBrandByIdUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindBrandByIdService implements FindBrandByIdUseCase {

    private final BrandRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDto> findById(Long id) {
        return repository.findById(id);
    }
}
