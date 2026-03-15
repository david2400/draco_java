package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_combo;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductComboEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface JpaProductComboRepository extends JpaRepository<ProductComboEntity, Long> {

    List<ProductComboEntity> findByComboId(Long comboId);

    List<ProductComboEntity> findByComboIdIn(Collection<Long> comboIds);
}
