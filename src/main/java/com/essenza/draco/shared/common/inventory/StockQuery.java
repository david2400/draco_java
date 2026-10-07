package com.essenza.draco.shared.common.inventory;

import java.util.Collection;
import java.util.Map;

/**
 * Consulta de stock expuesta por el inventario al resto de módulos (kernel
 * compartido): el catálogo la usa para mostrar existencias sin depender del
 * módulo de inventario.
 */
public interface StockQuery {

    /** Unidades físicas (on hand) por SKU, sumando todas las bodegas. */
    Map<Long, Integer> onHandBySku(Collection<Long> skuIds);

    /** Unidades disponibles para vender (on hand − reservadas, nunca negativo) por SKU. */
    Map<Long, Integer> availableBySku(Collection<Long> skuIds);
}
