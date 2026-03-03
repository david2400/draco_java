package com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.repositories.delivery_estimate;

import com.essenza.draco.modules.shipping_logistics.product_distribution.infrastructure.outbound.persistence.mysql.shop.DeliveryEstimateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryEstimateRepository extends JpaRepository<DeliveryEstimateEntity, Long> {
}
