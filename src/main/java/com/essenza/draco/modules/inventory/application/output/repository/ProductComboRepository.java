package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;

import java.util.List;
import java.util.Optional;

public interface ProductComboRepository {

    ProductComboDto create(CreateProductComboDto input);

    ProductComboDto update(Long id, UpdateProductComboDto input);

    boolean deleteById(Long id);

    Optional<ProductComboDto> findById(Long id);

    List<ProductComboDto> findAll();
}
