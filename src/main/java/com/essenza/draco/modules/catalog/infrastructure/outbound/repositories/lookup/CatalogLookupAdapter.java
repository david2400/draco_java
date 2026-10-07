package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.lookup;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductLookupDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.modules.catalog.application.output.repository.CatalogLookupQuery;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;
import com.essenza.draco.shared.common.lookup.NativeLookupSql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/** Búsquedas ligeras del catálogo con SQL nativo (solo las columnas que pinta el selector). */
@Component
public class CatalogLookupAdapter implements CatalogLookupQuery {

    private final EntityManager em;

    public CatalogLookupAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<ProductLookupDto> products(LookupRequest request, List<String> statuses) {
        NativeLookupSql sql = new NativeLookupSql("""
                SELECT p.id_product, p.name, p.slug, p.status, p.product_type, p.unit_price, p.image_url
                  FROM products p""")
                .where("p.deleted = 0")
                .match(request, "p.id_product", "p.name", "p.slug");
        if (!statuses.isEmpty() && !request.byIds()) {
            sql.where("p.status IN (:statuses)").param("statuses", statuses);
        }
        sql.orderBy("p.name").orderBy("p.id_product");
        return rows(sql, request.limit()).stream()
                .map(r -> new ProductLookupDto(id(r[0]), (String) r[1], (String) r[2], (String) r[3], (String) r[4],
                        money(r[5]), (String) r[6]))
                .toList();
    }

    @Override
    public List<SkuLookupDto> skus(LookupRequest request, Long productId, boolean sellableOnly) {
        NativeLookupSql sql = new NativeLookupSql("""
                SELECT s.id_sku, s.product_id, s.code, p.name, s.name, p.product_type, s.price, s.active, p.status,
                       s.legacy_child_id, COALESCE(NULLIF(s.image_url, ''), p.image_url)
                  FROM product_skus s
                  JOIN products p ON p.id_product = s.product_id AND p.deleted = 0""")
                .where("s.deleted = 0")
                .match(request, "s.id_sku", "s.code", "p.name", "s.name", "s.barcode");
        if (!request.byIds()) {
            // SKU por defecto desactivado de un producto que ahora tiene variantes: no es elegible.
            sql.where("NOT (p.product_type = 'VARIANT' AND s.legacy_child_id IS NULL)");
            if (sellableOnly) {
                sql.where("s.active = TRUE AND p.status = 'ACTIVE'");
            }
        }
        if (productId != null) {
            sql.where("s.product_id = :productId").param("productId", productId);
        }
        sql.orderBy("p.name").orderBy("s.id_sku");
        return rows(sql, request.limit()).stream().map(CatalogLookupAdapter::toSku).toList();
    }

    @Override
    public List<LookupOption> brands(LookupRequest request) {
        NativeLookupSql sql = new NativeLookupSql("SELECT b.id_brand, b.name, NULL FROM brands b")
                .where("b.deleted = 0")
                .match(request, "b.id_brand", "b.name")
                .orderBy("b.name");
        return options(sql, request.limit());
    }

    @Override
    public List<LookupOption> categories(LookupRequest request) {
        NativeLookupSql sql = new NativeLookupSql("SELECT c.id_category, c.name, NULL FROM categories c")
                .where("c.deleted = 0")
                .match(request, "c.id_category", "c.name")
                .orderBy("c.name");
        return options(sql, request.limit());
    }

    @Override
    public List<LookupOption> subcategories(LookupRequest request, Long categoryId) {
        NativeLookupSql sql = new NativeLookupSql("""
                SELECT s.id_subcategory, s.name, c.name
                  FROM subcategorys s
                  LEFT JOIN categories c ON c.id_category = s.category_id""")
                .where("s.deleted = 0")
                .match(request, "s.id_subcategory", "s.name");
        if (categoryId != null && !request.byIds()) {
            sql.where("s.category_id = :categoryId").param("categoryId", categoryId);
        }
        sql.orderBy("s.name");
        return options(sql, request.limit());
    }

    private List<LookupOption> options(NativeLookupSql sql, int limit) {
        return rows(sql, limit).stream().map(r -> new LookupOption(id(r[0]), (String) r[1], (String) r[2])).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(NativeLookupSql sql, int limit) {
        Query query = em.createNativeQuery(sql.sql(limit));
        sql.params().forEach(query::setParameter);
        return query.getResultList();
    }

    private static SkuLookupDto toSku(Object[] r) {
        String productName = (String) r[3];
        String skuName = (String) r[4];
        boolean variant = r[9] != null;
        String variantName = variant && skuName != null && !skuName.isBlank() && !skuName.equals(productName) ? skuName : null;
        String name = variantName != null ? productName + " · " + variantName : productName;
        boolean active = bool(r[7]);
        return new SkuLookupDto(id(r[0]), id(r[1]), (String) r[2], name, productName, variantName, (String) r[5],
                money(r[6]), active, active && "ACTIVE".equals(r[8]), 0, 0, (String) r[10]);
    }

    private static Long id(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private static BigDecimal money(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private static boolean bool(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof Number n) {
            return n.intValue() != 0;
        }
        if (value instanceof byte[] bytes) {
            return bytes.length > 0 && bytes[0] != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }
}
