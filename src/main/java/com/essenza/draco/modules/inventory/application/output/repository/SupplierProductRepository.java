package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** SKU que surte cada proveedor. */
public interface SupplierProductRepository {

    /** Con los datos vigentes del SKU (código, nombres y precio de venta). */
    List<View> listBySupplier(Long supplierId);

    Optional<View> findView(Long id);

    Optional<SupplierProduct> findById(Long id);

    SupplierProduct save(SupplierProduct product);

    void delete(Long id);

    void deleteBySupplier(Long supplierId);

    boolean existsSku(Long supplierId, Long skuId, Long excludeId);

    /** Quita la marca de preferido del SKU en los demás proveedores. */
    void clearPreferred(Long skuId, Long exceptId);

    /** Un proveedor bloqueado o inactivo deja de ser el preferido de sus SKU. */
    void clearPreferredOfSupplier(Long supplierId);

    record View(SupplierProduct product, String skuCode, String productName, String skuName, BigDecimal salePrice) {
    }
}
