package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.catalog.application.dto.brand.BrandDto;
import com.essenza.draco.modules.catalog.application.dto.brand.CreateBrandDto;
import com.essenza.draco.modules.catalog.application.dto.brand.UpdateBrandDto;

public interface BrandRepository {

    BrandDto create(CreateBrandDto input);

    BrandDto update(Long id, UpdateBrandDto input);

    boolean deleteById(Long id);

    Optional<BrandDto> findById(Long id);

    List<BrandDto> findAll();

    Optional<BrandDto> findByName(String name);

    Page<BrandDto> search(String query, Pageable pageable);

    boolean existsById(Long id);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese nombre? */
    boolean existsByName(String name, Long excludeId);

    /** ¿Existe otro registro (distinto de {@code excludeId}) con ese slug? */
    boolean existsBySlug(String slug, Long excludeId);

    long count();
}
