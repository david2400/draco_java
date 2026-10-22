package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.warehouse;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.WarehouseLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface JpaWarehouseLocationRepository extends JpaRepository<WarehouseLocationEntity, Long> {

    List<WarehouseLocationEntity> findByWarehouseIdOrderByCodeAsc(Long warehouseId);

    boolean existsByWarehouseIdAndCode(Long warehouseId, String code);

    boolean existsByWarehouseIdAndCodeAndIdNot(Long warehouseId, String code, Long id);

    @Query("SELECT l.warehouseId, COUNT(l) FROM WarehouseLocationEntity l WHERE l.warehouseId IN :ids GROUP BY l.warehouseId")
    List<Object[]> countByWarehouse(@Param("ids") Collection<Long> ids);
}
