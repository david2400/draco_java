package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.unit_measurement;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.UnitMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUnitMeasurementRepository extends JpaRepository<UnitMeasurementEntity, Long> {
}
