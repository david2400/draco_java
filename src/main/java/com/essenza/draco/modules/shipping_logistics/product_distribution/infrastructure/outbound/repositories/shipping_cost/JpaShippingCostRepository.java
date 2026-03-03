package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.shipping_cost;

import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.ShippingCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaShippingCostRepository extends JpaRepository<ShippingCostEntity, Long> {
}
