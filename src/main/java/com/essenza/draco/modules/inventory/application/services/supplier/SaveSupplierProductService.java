package com.essenza.draco.modules.inventory.application.services.supplier;

import com.essenza.draco.modules.inventory.application.dto.supplier.SaveSupplierProductDto;
import com.essenza.draco.modules.inventory.application.dto.supplier.SupplierProductDto;
import com.essenza.draco.modules.inventory.application.input.supplier.SaveSupplierProductUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.SupplierProductRepository;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierProduct;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierRuleViolation;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import com.essenza.draco.shared.common.catalog.SkuPricing;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alta o edición de un SKU que surte el proveedor. El SKU se valida contra el catálogo
 * (contrato {@link SkuPricing}); un SKU aparece una vez por proveedor y tiene a lo sumo un
 * proveedor preferido.
 */
@Service
@Transactional
public class SaveSupplierProductService implements SaveSupplierProductUseCase {

    private final SupplierProductRepository products;
    private final SkuPricing skus;
    private final SupplierSoporte soporte;

    public SaveSupplierProductService(SupplierProductRepository products, SkuPricing skus, SupplierSoporte soporte) {
        this.products = products;
        this.skus = skus;
        this.soporte = soporte;
    }

    @Override
    public SupplierProductDto save(Long supplierId, Long id, SaveSupplierProductDto input) {
        Supplier supplier = soporte.supplier(supplierId);
        if (id != null) {
            SupplierProduct current = products.findById(id)
                    .filter(p -> p.supplierId().equals(supplierId))
                    .orElseThrow(() -> new NotFoundException("El proveedor no surte el producto " + id + "."));
            if (!current.skuId().equals(input.skuId())) {
                throw new SupplierRuleViolation("No se cambia el SKU de un producto del proveedor: elimínalo y agrega el nuevo.");
            }
        }
        SkuPricing.SkuSnapshot sku = skus.resolve(null, input.skuId());
        if (products.existsSku(supplierId, sku.skuId(), id)) {
            throw new ConflictException("El proveedor ya surte el SKU " + sku.code() + ".");
        }
        boolean active = input.active() == null || input.active();
        boolean preferred = Boolean.TRUE.equals(input.preferred());
        if (preferred && supplier.status() != SupplierStatus.ACTIVE) {
            throw new SupplierRuleViolation("Solo un proveedor activo puede ser el preferido de un SKU.");
        }
        SupplierProduct saved = products.save(new SupplierProduct(id, supplierId, sku.productId(), sku.skuId(),
                input.supplierCode(), input.unitCost(), input.leadTimeDays(),
                input.minOrderQty() == null ? 1 : input.minOrderQty(), preferred, active));
        if (saved.preferred()) {
            products.clearPreferred(saved.skuId(), saved.id());
        }
        return soporte.productDto(products.findView(saved.id()).orElseThrow(), supplier);
    }
}
