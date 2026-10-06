package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.attribute;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;

import jakarta.persistence.EntityManager;

/**
 * Ficha técnica y ejes de variante con SQL directo: son tablas de valores que se
 * reemplazan en bloque (borrar e insertar) y no necesitan entidades.
 */
@Repository
public class ProductAttributesStoreAdapter implements ProductAttributesStore {

    private final EntityManager em;

    public ProductAttributesStoreAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ProductHeader> findProduct(Long productId) {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT id_product, status, template_id FROM products WHERE id_product = :id AND deleted = 0")
                .setParameter("id", productId).getResultList();
        return rows.stream().findFirst().map(row -> new ProductHeader(((Number) row[0]).longValue(), (String) row[1],
                row[2] == null ? null : ((Number) row[2]).longValue()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VariantSku> variantSkus(Long productId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT id_sku, code, name, active FROM product_skus
                 WHERE product_id = :id AND legacy_child_id IS NOT NULL AND deleted = 0
                 ORDER BY id_sku
                """).setParameter("id", productId).getResultList();
        return rows.stream().map(row -> new VariantSku(((Number) row[0]).longValue(), (String) row[1], (String) row[2],
                toBoolean(row[3]))).toList();
    }

    @Override
    public void setTemplate(Long productId, Long templateId) {
        em.createNativeQuery("UPDATE products SET template_id = :template WHERE id_product = :id")
                .setParameter("template", templateId)
                .setParameter("id", productId)
                .executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AttributeValue> values(Long productId) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT attribute_id, value_text, value_number, value_boolean, option_id
                  FROM product_attribute_values WHERE product_id = :id ORDER BY attribute_id
                """).setParameter("id", productId).getResultList();
        return rows.stream().map(row -> new AttributeValue(((Number) row[0]).longValue(), (String) row[1],
                toDecimal(row[2]), row[3] == null ? null : toBoolean(row[3]),
                row[4] == null ? null : ((Number) row[4]).longValue())).toList();
    }

    @Override
    public void replaceValues(Long productId, List<AttributeValue> values) {
        em.createNativeQuery("DELETE FROM product_attribute_values WHERE product_id = :id")
                .setParameter("id", productId).executeUpdate();
        for (AttributeValue value : values) {
            em.createNativeQuery("""
                    INSERT INTO product_attribute_values
                        (product_id, attribute_id, value_text, value_number, value_boolean, option_id)
                    VALUES (:product, :attribute, :text, :number, :bool, :option)
                    """)
                    .setParameter("product", productId)
                    .setParameter("attribute", value.attributeId())
                    .setParameter("text", value.text())
                    .setParameter("number", value.number())
                    .setParameter("bool", value.bool())
                    .setParameter("option", value.optionId())
                    .executeUpdate();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Map<Long, Long>> skuOptions(Collection<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = em.createNativeQuery(
                "SELECT sku_id, attribute_id, option_id FROM sku_attribute_values WHERE sku_id IN (:ids)")
                .setParameter("ids", skuIds).getResultList();
        Map<Long, Map<Long, Long>> result = new HashMap<>();
        for (Object[] row : rows) {
            result.computeIfAbsent(((Number) row[0]).longValue(), key -> new LinkedHashMap<>())
                    .put(((Number) row[1]).longValue(), ((Number) row[2]).longValue());
        }
        return result;
    }

    @Override
    public void replaceSkuOptions(Collection<Long> skuIds, List<VariantSelection> selections) {
        if (skuIds == null || skuIds.isEmpty()) {
            return;
        }
        em.createNativeQuery("DELETE FROM sku_attribute_values WHERE sku_id IN (:ids)")
                .setParameter("ids", skuIds).executeUpdate();
        for (VariantSelection selection : selections) {
            selection.optionsByAttribute().forEach((attributeId, optionId) -> em.createNativeQuery(
                            "INSERT INTO sku_attribute_values (sku_id, attribute_id, option_id) VALUES (:sku, :attribute, :option)")
                    .setParameter("sku", selection.skuId())
                    .setParameter("attribute", attributeId)
                    .setParameter("option", optionId)
                    .executeUpdate());
        }
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        if (value instanceof byte[] bytes) {
            return bytes.length > 0 && bytes[0] != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private static BigDecimal toDecimal(Object value) {
        if (value == null) {
            return null;
        }
        BigDecimal number = value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
        // DECIMAL(18,4) devuelve 2.5000: se quitan los ceros a la derecha para mostrar 2.5.
        BigDecimal stripped = number.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }
}
