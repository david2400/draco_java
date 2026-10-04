package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.subcategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.SubcategoryEntity;

@Repository
public interface JpaSubcategoryRepository extends JpaRepository<SubcategoryEntity, Long>, JpaSpecificationExecutor<SubcategoryEntity> {

    Optional<SubcategoryEntity> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    long countByCategoryId(Long categoryId);
}
