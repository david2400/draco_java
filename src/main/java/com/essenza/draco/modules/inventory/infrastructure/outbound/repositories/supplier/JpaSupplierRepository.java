package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSupplierRepository extends JpaRepository<SupplierEntity, Long>, JpaSpecificationExecutor<SupplierEntity> {

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByCode(String code);

    boolean existsByDocTypeAndDocNumberAndIdNot(String docType, String docNumber, Long id);

    boolean existsByDocTypeAndDocNumber(String docType, String docNumber);
}
