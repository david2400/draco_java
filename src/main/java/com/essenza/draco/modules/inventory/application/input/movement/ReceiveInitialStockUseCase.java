package com.essenza.draco.modules.inventory.application.input.movement;

public interface ReceiveInitialStockUseCase {

    /** Entrada de stock inicial de un SKU en una bodega (principal si es nula); 0 no hace nada. */
    void receiveInitial(Long productId, Long skuId, Long warehouseId, int quantity, String source);
}
