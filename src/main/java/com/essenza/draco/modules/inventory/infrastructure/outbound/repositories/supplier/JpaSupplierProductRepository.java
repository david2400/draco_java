package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaSupplierProductRepository extends JpaRepository<SupplierProductEntity, Long> {

    boolean existsBySupplierIdAndSkuIdAndIdNot(Long supplierId, Long skuId, Long id);

    boolean existsBySupplierIdAndSkuId(Long supplierId, Long skuId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE SupplierProductEntity p SET p.preferred = false WHERE p.skuId = :skuId AND p.id <> :exceptId AND p.preferred = true")
    int clearPreferred(@Param("skuId") Long skuId, @Param("exceptId") Long exceptId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE SupplierProductEntity p SET p.preferred = false WHERE p.supplierId = :supplierId AND p.preferred = true")
    int clearPreferredOfSupplier(@Param("supplierId") Long supplierId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM SupplierProductEntity p WHERE p.supplierId = :supplierId")
    void deleteBySupplier(@Param("supplierId") Long supplierId);
}
