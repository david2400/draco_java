package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_child;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductChildRepository extends JpaRepository<ProductChildEntity, Long> {
}
