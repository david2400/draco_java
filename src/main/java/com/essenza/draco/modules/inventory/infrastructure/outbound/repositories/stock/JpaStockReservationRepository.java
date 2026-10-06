package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockReservationEntity;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationEntity, Long> {

    List<StockReservationEntity> findByOrderLineIdAndStatusOrderById(Long orderLineId, String status);

    List<StockReservationEntity> findByOrderIdOrderById(Long orderId);
}
