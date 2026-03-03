package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.brand;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaBrandRepository extends JpaRepository<BrandEntity, Long> {
    Optional<BrandEntity> findByName(String name);
}
