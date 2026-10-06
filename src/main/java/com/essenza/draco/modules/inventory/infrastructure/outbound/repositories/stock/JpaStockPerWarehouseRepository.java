package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockPerWarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaStockPerWarehouseRepository extends JpaRepository<StockPerWarehouseEntity, Long> {
    Optional<StockPerWarehouseEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    /** Unidades totales almacenadas en una bodega (para impedir borrarla con stock). */
    @Query("select coalesce(sum(s.quantity), 0) from StockPerWarehouseEntity s where s.warehouseId = :warehouseId")
    long sumQuantityByWarehouseId(@Param("warehouseId") Long warehouseId);

    java.util.List<com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockPerWarehouseEntity> findByProductId(Long productId);
}
