package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand;

import com.essenza.draco.modules.catalog.application.output.repository.BrandRepository;
import com.essenza.draco.modules.catalog.domain.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.domain.dto.brand.UpdateBrandDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.BrandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BrandRepositoryAdapter implements BrandRepository {

    private final JpaBrandRepository brandJpaRepository;
    private final BrandMapper brandMapper;

    public BrandRepositoryAdapter(JpaBrandRepository brandJpaRepository, BrandMapper brandMapper) {
        this.brandJpaRepository = brandJpaRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public BrandDto create(CreateBrandDto input) {
        var entity = brandMapper.toEntity(input);
        var saved = brandJpaRepository.save(entity);
        return brandMapper.toDto(saved);
    }

    @Override
    public BrandDto update(Long id, UpdateBrandDto input) {
        var entity = brandJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brandMapper.updateEntityFromDto(input, entity);
        var updated = brandJpaRepository.save(entity);
        return brandMapper.toDto(updated);
    }

    @Override
    public boolean deleteById(Long id) {
        if (brandJpaRepository.existsById(id)) {
            brandJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<BrandDto> findById(Long id) {
        return brandJpaRepository.findById(id).map(brandMapper::toDto);
    }

    @Override
    public List<BrandDto> findAll() {
        return brandJpaRepository.findAll()
                .stream()
                .map(brandMapper::toDto)
                .toList();
    }

    @Override
    public Optional<BrandDto> findByName(String name) {
        return brandJpaRepository.findByName(name).map(brandMapper::toDto);
    }

    // 🔥 Aquí puedes exponer más métodos JPA si lo necesitas:
    public boolean existsById(Long id) {
        return brandJpaRepository.existsById(id);
    }

    public long count() {
        return brandJpaRepository.count();
    }
}
