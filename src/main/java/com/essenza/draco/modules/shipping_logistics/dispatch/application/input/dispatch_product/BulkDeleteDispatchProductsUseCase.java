package com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product;

import java.util.List;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Eliminación (lógica) en lote; cada id se valida por separado. */
public interface BulkDeleteDispatchProductsUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
