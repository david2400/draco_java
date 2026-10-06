package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock;

import com.essenza.draco.modules.inventory.application.output.repository.LegacyStockProjectionPort;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.StockPerWarehouseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Proyección de {@code stock_items} sobre las columnas heredadas mientras
 * convivan los dos modelos (se elimina en la Fase 7):
 * <ul>
 *   <li>{@code product_childs.stock} = total del SKU de la variante;</li>
 *   <li>{@code products.stock} = total de los SKUs vigentes del producto;</li>
 *   <li>{@code stock_per_warehouse} = total del producto por bodega.</li>
 * </ul>
 */
@Component
public class LegacyStockProjectionAdapter implements LegacyStockProjectionPort {

    @PersistenceContext
    private EntityManager em;

    private final JpaStockPerWarehouseRepository perWarehouse;

    public LegacyStockProjectionAdapter(JpaStockPerWarehouseRepository perWarehouse) {
        this.perWarehouse = perWarehouse;
    }

    @Override
    public void refresh(Long productId) {
        em.createNativeQuery("""
                update product_childs c
                join product_skus s on s.legacy_child_id = c.id_product_child
                set c.stock = (select coalesce(sum(i.on_hand), 0) from stock_items i
                               where i.sku_id = s.id_sku and i.deleted = 0)
                where s.product_id = :p
                """).setParameter("p", productId).executeUpdate();
        em.createNativeQuery("""
                update products p
                set p.stock = (select coalesce(sum(i.on_hand), 0) from stock_items i
                               join product_skus s on s.id_sku = i.sku_id and s.deleted = 0
                               where i.product_id = p.id_product and i.deleted = 0)
                where p.id_product = :p
                """).setParameter("p", productId).executeUpdate();

        Map<Long, int[]> byWarehouse = new HashMap<>();
        List<?> rows = em.createNativeQuery("""
                select i.warehouse_id, coalesce(sum(i.on_hand), 0), coalesce(max(i.min_threshold), 0)
                from stock_items i join product_skus s on s.id_sku = i.sku_id and s.deleted = 0
                where i.product_id = :p and i.deleted = 0 group by i.warehouse_id
                """).setParameter("p", productId).getResultList();
        for (Object row : rows) {
            Object[] r = (Object[]) row;
            byWarehouse.put(((Number) r[0]).longValue(), new int[]{((Number) r[1]).intValue(), ((Number) r[2]).intValue()});
        }
        for (StockPerWarehouseEntity existing : perWarehouse.findByProductId(productId)) {
            int[] totals = byWarehouse.remove(existing.getWarehouseId());
            existing.setQuantity(totals == null ? 0 : totals[0]);
            perWarehouse.save(existing);
        }
        byWarehouse.forEach((warehouseId, totals) -> perWarehouse.save(StockPerWarehouseEntity.builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .quantity(totals[0])
                .minThreshold(totals[1])
                .build()));
    }
}
