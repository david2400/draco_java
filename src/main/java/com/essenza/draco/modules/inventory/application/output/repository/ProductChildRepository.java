package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;

import java.util.List;
import java.util.Optional;

public interface ProductChildRepository {
    ProductChildDto create(CreateProductChildDto input);

    ProductChildDto update(Long id, UpdateProductChildDto input);

    boolean deleteById(Long id);

    Optional<ProductChildDto> findById(Long id);

    List<ProductChildDto> findAll();
}
