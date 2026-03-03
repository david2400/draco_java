package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockPerWarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaStockPerWarehouseRepository extends JpaRepository<StockPerWarehouseEntity, Long> {
    Optional<StockPerWarehouseEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
