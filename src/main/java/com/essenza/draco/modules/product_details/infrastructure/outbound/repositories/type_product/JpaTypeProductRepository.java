package com.essenza.draco.modules.product_details.infrastructure.outbound.repositories.type_product;

import com.essenza.draco.modules.product_details.infrastructure.outbound.persistence.mysql.shop.TypeProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTypeProductRepository extends JpaRepository<TypeProductEntity, Long> {
}
