package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface JpaSupplierContactRepository extends JpaRepository<SupplierContactEntity, Long> {

    List<SupplierContactEntity> findBySupplierIdInOrderBySupplierIdAscSortOrderAsc(Collection<Long> supplierIds);

    @Modifying(flushAutomatically = true, clearAutomatically = false)
    @Query("DELETE FROM SupplierContactEntity c WHERE c.supplierId = :supplierId")
    void deleteBySupplier(@Param("supplierId") Long supplierId);
}
