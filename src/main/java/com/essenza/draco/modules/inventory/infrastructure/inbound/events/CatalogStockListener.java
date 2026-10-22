package com.essenza.draco.modules.inventory.infrastructure.inbound.events;

import com.essenza.draco.modules.inventory.application.input.stock.AlignStockUseCase;
import com.essenza.draco.shared.common.inventory.StockLevelRequested;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Escucha las peticiones de stock del catálogo (formularios heredados) y las
 * convierte en movimientos de ajuste. Síncrono y en la misma transacción: si el
 * ajuste no es posible, se revierte también el cambio del producto.
 */
@Component
public class CatalogStockListener {

    private final AlignStockUseCase alignStock;
    private final com.essenza.draco.modules.inventory.application.input.movement.ReceiveInitialStockUseCase initialStock;

    public CatalogStockListener(AlignStockUseCase alignStock,
                                com.essenza.draco.modules.inventory.application.input.movement.ReceiveInitialStockUseCase initialStock) {
        this.alignStock = alignStock;
        this.initialStock = initialStock;
    }

    @EventListener
    public void on(StockLevelRequested event) {
        alignStock.align(event.productId(), event.skuId(), event.desiredOnHand(), event.source());
    }

    /** Stock inicial de variantes generadas: entrada en la bodega elegida. */
    @EventListener
    public void on(com.essenza.draco.shared.common.inventory.StockEntryRequested event) {
        initialStock.receiveInitial(event.productId(), event.skuId(), event.warehouseId(), event.quantity(), event.source());
    }
}
