package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.application.input.brand.*;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand.BrandRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandServiceImpl implements CreateBrandUseCase, UpdateBrandUseCase, DeleteBrandByIdUseCase, FindAllBrandsUseCase, FindBrandByIdUseCase {

    private final  BrandRepositoryAdapter brandRepository;

    public BrandServiceImpl(BrandRepositoryAdapter brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandDto create(CreateBrandDto input) {
        return brandRepository.create(input);
    }

    @Override
    public BrandDto update(Long id, UpdateBrandDto input) {
        return brandRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return brandRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDto> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> findAll() {
        return brandRepository.findAll();
    }
}
