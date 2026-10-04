package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.BrandEntity;

@Repository
public interface JpaBrandRepository extends JpaRepository<BrandEntity, Long>, JpaSpecificationExecutor<BrandEntity> {

    Optional<BrandEntity> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);
}
