package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.unit_measurement;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.UnitMeasurementEntity;

@Repository
public interface JpaUnitMeasurementRepository extends JpaRepository<UnitMeasurementEntity, Long> {

    Optional<UnitMeasurementEntity> findFirstByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByCode(String code);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByNameIgnoreCase(String name);
}
