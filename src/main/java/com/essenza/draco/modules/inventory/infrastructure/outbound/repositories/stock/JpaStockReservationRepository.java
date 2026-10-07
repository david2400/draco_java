package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockReservationEntity;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationEntity, Long> {

    List<StockReservationEntity> findByOrderLineIdAndStatusOrderById(Long orderLineId, String status);

    List<StockReservationEntity> findByOrderIdOrderById(Long orderId);

    @Query("""
            SELECT r FROM StockReservationEntity r
             WHERE (:orderId IS NULL OR r.orderId = :orderId)
               AND (:skuId IS NULL OR r.skuId = :skuId)
               AND (:productId IS NULL OR r.productId = :productId)
               AND (:status IS NULL OR r.status = :status)
             ORDER BY r.id DESC""")
    List<StockReservationEntity> search(@Param("orderId") Long orderId, @Param("skuId") Long skuId,
                                        @Param("productId") Long productId, @Param("status") String status,
                                        Pageable pageable);
}
