package com.essenza.draco.modules.catalog.application.output.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Persistencia del generador de variantes: variantes (product_childs + SKU), ejes e imágenes por valor. */
public interface VariantMatrixStore {

    record ProductInfo(Long id, String name, BigDecimal price, BigDecimal cost, Long templateId, boolean combo) {
    }

    record VariantRecord(Long childId, Long skuId, String skuCode, String name, BigDecimal price, BigDecimal cost,
                         BigDecimal compareAt, String barcode, String imageUrl, boolean active, int stock) {
    }

    record AxisRecord(Long attributeId, int position, boolean imageAxis) {
    }

    record ChildData(String name, BigDecimal price, BigDecimal cost, String imageUrl, boolean active) {
    }

    Optional<ProductInfo> product(Long productId);

    /** Variantes vigentes con su SKU (puede faltar si aún no se sincronizó). */
    List<VariantRecord> variants(Long productId);

    Long createChild(Long productId, ChildData data);

    void updateChild(Long childId, ChildData data);

    void setChildActive(Long childId, boolean active);

    void deleteChild(Long childId);

    /** Sincroniza los SKU del producto; devuelve variante → SKU. */
    Map<Long, Long> syncSkus(Long productId);

    void updateSkuExtras(Long skuId, String code, String barcode, BigDecimal compareAt);

    boolean codeTaken(String code, Long excludeSkuId);

    /** SKU (de cualquier producto, no borrado) que ya usa el código de barras. */
    boolean barcodeTaken(String barcode, Collection<Long> excludeSkuIds);

    /** SKU que tuvieron stock, reservas o ventas. */
    Set<Long> withHistory(Collection<Long> skuIds);

    List<AxisRecord> axes(Long productId);

    void replaceAxes(Long productId, List<AxisRecord> axes);

    Map<Long, String> optionImages(Long productId);

    void replaceOptionImages(Long productId, Map<Long, String> images);

    /** Crea el valor en el atributo (al final) o devuelve el existente con el mismo texto. */
    Long ensureOption(Long attributeId, String value);
}
