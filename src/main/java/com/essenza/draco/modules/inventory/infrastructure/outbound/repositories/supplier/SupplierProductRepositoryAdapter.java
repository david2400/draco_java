package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.supplier;

import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierProduct;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.SupplierProductEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * SKU por proveedor en {@code supplier_products}. La vista une los datos vigentes del SKU
 * (lectura de solo consulta sobre las tablas del catálogo).
 */
@Repository
public class SupplierProductRepositoryAdapter implements SupplierProductRepository {

    private static final String VIEW = """
            SELECT sp.id_supplier_product, sp.supplier_id, sp.product_id, sp.sku_id, sp.supplier_code, sp.unit_cost,
                   sp.lead_time_days, sp.min_order_qty, sp.preferred, sp.active,
                   k.code, p.name, k.name, k.price
              FROM supplier_products sp
              LEFT JOIN product_skus k ON k.id_sku = sp.sku_id
              LEFT JOIN products p ON p.id_product = sp.product_id
            """;

    private final JpaSupplierProductRepository jpa;
    private final EntityManager em;

    public SupplierProductRepositoryAdapter(JpaSupplierProductRepository jpa, EntityManager em) {
        this.jpa = jpa;
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<View> listBySupplier(Long supplierId) {
        List<Object[]> rows = em.createNativeQuery(VIEW + " WHERE sp.supplier_id = :id ORDER BY sp.active DESC, p.name, k.name")
                .setParameter("id", supplierId).getResultList();
        return rows.stream().map(SupplierProductRepositoryAdapter::toView).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<View> findView(Long id) {
        List<Object[]> rows = em.createNativeQuery(VIEW + " WHERE sp.id_supplier_product = :id")
                .setParameter("id", id).getResultList();
        return rows.stream().findFirst().map(SupplierProductRepositoryAdapter::toView);
    }

    @Override
    public Optional<SupplierProduct> findById(Long id) {
        return jpa.findById(id).map(SupplierProductRepositoryAdapter::toDomain);
    }

    @Override
    public SupplierProduct save(SupplierProduct p) {
        SupplierProductEntity e = p.id() == null ? new SupplierProductEntity()
                : jpa.findById(p.id()).orElseGet(SupplierProductEntity::new);
        e.setSupplierId(p.supplierId());
        e.setProductId(p.productId());
        e.setSkuId(p.skuId());
        e.setSupplierCode(p.supplierCode());
        e.setUnitCost(p.unitCost());
        e.setLeadTimeDays(p.leadTimeDays());
        e.setMinOrderQty(p.minOrderQty());
        e.setPreferred(p.preferred());
        e.setActive(p.active());
        return toDomain(jpa.saveAndFlush(e));
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void deleteBySupplier(Long supplierId) {
        jpa.deleteBySupplier(supplierId);
    }

    @Override
    public boolean existsSku(Long supplierId, Long skuId, Long excludeId) {
        return excludeId == null ? jpa.existsBySupplierIdAndSkuId(supplierId, skuId)
                : jpa.existsBySupplierIdAndSkuIdAndIdNot(supplierId, skuId, excludeId);
    }

    @Override
    public void clearPreferred(Long skuId, Long exceptId) {
        jpa.clearPreferred(skuId, exceptId);
    }

    @Override
    public void clearPreferredOfSupplier(Long supplierId) {
        jpa.clearPreferredOfSupplier(supplierId);
    }

    private static SupplierProduct toDomain(SupplierProductEntity e) {
        return new SupplierProduct(e.getId(), e.getSupplierId(), e.getProductId(), e.getSkuId(), e.getSupplierCode(),
                e.getUnitCost(), e.getLeadTimeDays(), e.getMinOrderQty() == null ? 1 : e.getMinOrderQty(),
                Boolean.TRUE.equals(e.getPreferred()), !Boolean.FALSE.equals(e.getActive()));
    }

    private static View toView(Object[] r) {
        boolean active = bool(r[9]);
        SupplierProduct p = new SupplierProduct(lng(r[0]), lng(r[1]), lng(r[2]), lng(r[3]), (String) r[4],
                (BigDecimal) r[5], r[6] == null ? null : ((Number) r[6]).intValue(), ((Number) r[7]).intValue(),
                bool(r[8]) && active, active);
        return new View(p, (String) r[10], (String) r[11], (String) r[12], (BigDecimal) r[13]);
    }

    private static Long lng(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private static boolean bool(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        return value instanceof Number n && n.intValue() != 0;
    }
}
