package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.inventory.application.output.repository.StockReservationRepository;
import com.essenza.draco.modules.inventory.domain.model.StockReservation;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockReservationEntity;

@Repository
public class StockReservationRepositoryAdapter implements StockReservationRepository {

    private final JpaStockReservationRepository jpa;

    public StockReservationRepositoryAdapter(JpaStockReservationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<StockReservation> activeByLine(Long lineId) {
        return jpa.findByOrderLineIdAndStatusOrderById(lineId, StockReservation.Status.ACTIVE.name()).stream()
                .map(StockReservationRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<StockReservation> byOrder(Long orderId) {
        return jpa.findByOrderIdOrderById(orderId).stream().map(StockReservationRepositoryAdapter::toDomain).toList();
    }

    @Override
    public StockReservation save(StockReservation reservation) {
        StockReservationEntity entity = reservation.getId() == null ? new StockReservationEntity()
                : jpa.findById(reservation.getId()).orElseThrow();
        entity.setOrderId(reservation.getOrderId());
        entity.setOrderLineId(reservation.getLineId());
        entity.setSkuId(reservation.getSkuId());
        entity.setProductId(reservation.getProductId());
        entity.setWarehouseId(reservation.getWarehouseId());
        entity.setQuantity(reservation.getQuantity());
        entity.setStatus(reservation.getStatus().name());
        entity.setExpiresAt(reservation.getExpiresAt());
        return toDomain(jpa.save(entity));
    }

    static StockReservation toDomain(StockReservationEntity entity) {
        return new StockReservation(entity.getId(), entity.getOrderId(), entity.getOrderLineId(), entity.getSkuId(),
                entity.getProductId(), entity.getWarehouseId(), entity.getQuantity(),
                StockReservation.Status.valueOf(entity.getStatus()), entity.getExpiresAt());
    }
}
