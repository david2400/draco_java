package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.carrier;

import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.CarrierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCarrierRepository extends JpaRepository<CarrierEntity, Long> {
}
