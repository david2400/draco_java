package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.order;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.sales.application.output.repository.OrderReferences;

import jakarta.persistence.EntityManager;

/** Tiendas, bodegas, transportadoras y categorías por SQL nativo (sin depender de otros módulos). */
@Repository
public class OrderReferencesAdapter implements OrderReferences {

    private final EntityManager em;

    public OrderReferencesAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<StoreRef> store(Long storeId) {
        if (storeId == null) {
            return Optional.empty();
        }
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT id_store, name, warehouse_id FROM stores
                         WHERE id_store = :id AND deleted = 0 AND active = 1""")
                .setParameter("id", storeId).getResultList();
        return rows.stream().findFirst().map(r -> new StoreRef(((Number) r[0]).longValue(), (String) r[1],
                r[2] == null ? null : ((Number) r[2]).longValue()));
    }

    @Override
    public boolean warehouseActive(Long warehouseId) {
        return count("SELECT COUNT(*) FROM warehouses WHERE id_warehouse = :id AND deleted = 0 AND active = 1", warehouseId) > 0;
    }

    @Override
    public boolean carrierActive(Long carrierId) {
        return count("SELECT COUNT(*) FROM carriers WHERE id_carrier = :id AND deleted = 0 AND is_active = 1", carrierId) > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Long> categories(Collection<Long> productIds) {
        Map<Long, Long> result = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return result;
        }
        List<Object[]> rows = em.createNativeQuery("SELECT id_product, category_id FROM products WHERE id_product IN (:ids)")
                .setParameter("ids", productIds).getResultList();
        rows.forEach(r -> {
            if (r[1] != null) {
                result.put(((Number) r[0]).longValue(), ((Number) r[1]).longValue());
            }
        });
        return result;
    }

    private long count(String sql, Long id) {
        if (id == null) {
            return 0;
        }
        return ((Number) em.createNativeQuery(sql).setParameter("id", id).getSingleResult()).longValue();
    }
}
