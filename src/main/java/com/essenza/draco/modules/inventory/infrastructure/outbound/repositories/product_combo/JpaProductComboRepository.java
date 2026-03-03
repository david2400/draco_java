package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_combo;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductComboEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductComboRepository extends JpaRepository<ProductComboEntity, Long> {
}
