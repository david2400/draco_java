package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.lookup;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductStatsDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductStatsQuery;

import jakarta.persistence.EntityManager;

/**
 * Agregados con SQL nativo. El stock usa {@code products.stock}, la proyección que el
 * inventario mantiene desde {@code stock_items} (F3), para no cruzar módulos.
 */
@Component
public class ProductStatsAdapter implements ProductStatsQuery {

    private final EntityManager em;

    public ProductStatsAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProductStatsDto stats(int threshold, int limit) {
        Object[] row = (Object[]) em.createNativeQuery("""
                SELECT COUNT(*),
                       COALESCE(SUM(CASE WHEN p.status = 'ACTIVE' THEN 1 ELSE 0 END), 0),
                       COALESCE(SUM(CASE WHEN COALESCE(p.stock, 0) <= 0 THEN 1 ELSE 0 END), 0),
                       COALESCE(SUM(CASE WHEN COALESCE(p.stock, 0) > 0 AND p.stock <= :threshold THEN 1 ELSE 0 END), 0),
                       COALESCE(SUM(GREATEST(COALESCE(p.stock, 0), 0) * COALESCE(p.real_price, 0)), 0)
                  FROM products p
                 WHERE p.deleted = 0""")
                .setParameter("threshold", threshold)
                .getSingleResult();
        List<ProductStatsDto.LowStockItem> low = limit == 0 ? List.of()
                : ((List<Object[]>) em.createNativeQuery("""
                        SELECT p.id_product, p.name, COALESCE(p.stock, 0)
                          FROM products p
                         WHERE p.deleted = 0 AND COALESCE(p.stock, 0) <= :threshold
                         ORDER BY COALESCE(p.stock, 0), p.name""" + " LIMIT " + limit)
                        .setParameter("threshold", threshold)
                        .getResultList()).stream()
                .map(r -> new ProductStatsDto.LowStockItem(((Number) r[0]).longValue(), (String) r[1], ((Number) r[2]).intValue()))
                .toList();
        return new ProductStatsDto(num(row[0]), num(row[1]), num(row[2]), num(row[3]), threshold,
                new BigDecimal(row[4].toString()).setScale(2, java.math.RoundingMode.HALF_UP), low);
    }

    private static long num(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }
}
