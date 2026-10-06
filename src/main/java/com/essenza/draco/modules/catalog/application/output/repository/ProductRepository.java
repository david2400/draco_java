package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductFilter;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    ProductDto create(CreateProductDto input);

    ProductDto update(Long id, UpdateProductDto input);

    boolean deleteById(Long id);

    Optional<ProductDto> findById(Long id);

    Page<ProductDto> findAll(Pageable pageable);

    Page<ProductDto> findAll(ProductFilter filter, Pageable pageable);

    Long save(Product product);

    /** ¿Existe otro producto (distinto de {@code excludeId}) con ese slug? */
    boolean existsBySlug(String slug, Long excludeId);
}
