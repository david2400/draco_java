package com.essenza.draco.modules.inventory.application.output.repository;

import java.util.List;

import com.essenza.draco.modules.inventory.domain.model.StockReservation;

/** Reservas de stock por línea de orden. */
public interface StockReservationRepository {

    List<StockReservation> activeByLine(Long lineId);

    List<StockReservation> byOrder(Long orderId);

    StockReservation save(StockReservation reservation);
}
