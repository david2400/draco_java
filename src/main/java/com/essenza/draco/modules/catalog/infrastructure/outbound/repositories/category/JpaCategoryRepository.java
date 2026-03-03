package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.category;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String name);
}
