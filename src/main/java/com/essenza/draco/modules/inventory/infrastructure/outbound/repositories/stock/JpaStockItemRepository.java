package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockItemEntity;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaStockItemRepository extends JpaRepository<StockItemEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockItemEntity s where s.skuId = :skuId and s.warehouseId = :warehouseId")
    Optional<StockItemEntity> findForUpdate(@Param("skuId") Long skuId, @Param("warehouseId") Long warehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockItemEntity s where s.skuId = :skuId order by s.warehouseId")
    List<StockItemEntity> findAllForUpdateBySku(@Param("skuId") Long skuId);

    @Query("select coalesce(sum(s.onHand), 0) from StockItemEntity s where s.skuId = :skuId")
    long sumOnHandBySku(@Param("skuId") Long skuId);

    @Query(value = "select coalesce(sum(i.on_hand), 0) from stock_items i "
            + "join product_skus s on s.id_sku = i.sku_id and s.deleted = 0 "
            + "where i.warehouse_id = :warehouseId and i.deleted = 0", nativeQuery = true)
    long sumOnHandInWarehouse(@Param("warehouseId") Long warehouseId);

    List<StockItemEntity> findBySkuIdIn(Collection<Long> skuIds);

    @Query("select s from StockItemEntity s where (:skuId is null or s.skuId = :skuId) "
            + "and (:productId is null or s.productId = :productId) "
            + "and (:warehouseId is null or s.warehouseId = :warehouseId) order by s.productId, s.skuId, s.warehouseId")
    List<StockItemEntity> search(@Param("skuId") Long skuId, @Param("productId") Long productId,
                                 @Param("warehouseId") Long warehouseId);
}
