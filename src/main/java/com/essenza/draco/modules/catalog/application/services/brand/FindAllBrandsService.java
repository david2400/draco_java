package com.essenza.draco.modules.catalog.application.services.brand;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.input.brand.FindAllBrandsUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FindAllBrandsService implements FindAllBrandsUseCase {

    private final BrandRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> findAll() {
        return repository.findAll();
    }
}
