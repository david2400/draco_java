package com.essenza.draco.shared.common.inventory;

import java.util.List;

/**
 * Contrato entre devoluciones e inventario: reingresa al stock las unidades devueltas
 * (entrada "DEVOLUTION" en el kardex). Se une a la transacción de quien lo llama.
 */
public interface StockReturns {

    /**
     * Reingresa las unidades en la bodega indicada ({@code null} = bodega principal).
     *
     * @throws com.essenza.draco.shared.exceptions.NotFoundException si la bodega no existe
     */
    void restock(Long devolutionId, Long warehouseId, List<ReturnedUnit> units);

    /** Unidades de un SKU ({@code skuId} nulo = SKU por defecto del producto, líneas antiguas). */
    record ReturnedUnit(Long productId, Long skuId, int quantity) {
    }
}
