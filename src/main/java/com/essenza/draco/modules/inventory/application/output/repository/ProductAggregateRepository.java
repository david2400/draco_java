package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductAggregateRepository {

    Optional<Product> findById(Long id);

    List<Product> findAllByIds(Collection<Long> ids);
}
