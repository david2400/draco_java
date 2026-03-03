package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.SubcategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSubcategoryRepository extends JpaRepository<SubcategoryEntity, Long> {
    Optional<SubcategoryEntity> findByName(String name);
}
