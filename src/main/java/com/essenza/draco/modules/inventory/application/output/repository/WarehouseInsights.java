package com.essenza.draco.modules.inventory.application.output.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Lecturas de reporte de una bodega: existencias valorizadas (costo del SKU), movimientos y tiendas
 * o cajas que venden desde ella. Solo consulta; no cambia datos.
 */
public interface WarehouseInsights {

    Map<Long, StockSummary> summaries(Collection<Long> warehouseIds);

    StockSummary totals();

    Page<StockLine> stock(Long warehouseId, String q, boolean belowMinOnly, Pageable pageable);

    java.util.Optional<StockLine> line(Long warehouseId, Long skuId);

    List<MovementLine> movements(Long warehouseId, int limit);

    List<StoreLink> stores(Long warehouseId);

    List<RegisterLink> registers(Long warehouseId);

    /** Tiendas activas que venden desde la bodega. */
    long activeStores(Long warehouseId);

    /** Tiendas activas por bodega. */
    Map<Long, Long> activeStoreCounts(Collection<Long> warehouseIds);

    record StockSummary(long skus, long onHand, long reserved, BigDecimal value, long belowMin) {
        public static final StockSummary EMPTY = new StockSummary(0, 0, 0, BigDecimal.ZERO, 0);
    }

    record StockLine(Long skuId, Long productId, String skuCode, String productName, String skuName, int onHand,
                     int reserved, int minThreshold, BigDecimal unitCost) {
    }

    record MovementLine(Long id, String type, Long fromWarehouseId, Long toWarehouseId, Long skuId, String skuCode,
                        String productName, int quantity, String fromName, String toName, String reason,
                        String referenceType, Long referenceId, Instant createdAt) {
    }

    record StoreLink(Long id, String code, String name, boolean active) {
    }

    record RegisterLink(Long id, String name, String storeName, boolean active, boolean own) {
    }
}
