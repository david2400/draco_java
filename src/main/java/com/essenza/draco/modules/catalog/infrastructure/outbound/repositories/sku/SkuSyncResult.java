package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import java.util.Map;

/** Resultado de sincronizar los SKUs de un producto: SKU por defecto y SKU de cada variante. */
public record SkuSyncResult(Long defaultSkuId, Map<Long, Long> skuByChildId) {

    public static SkuSyncResult empty() {
        return new SkuSyncResult(null, Map.of());
    }
}
