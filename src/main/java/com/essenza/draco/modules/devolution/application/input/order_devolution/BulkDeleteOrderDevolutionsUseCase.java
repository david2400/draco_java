package com.essenza.draco.modules.devolution.application.input.order_devolution;

import java.util.List;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Eliminación (lógica) en lote; cada id se valida por separado. */
public interface BulkDeleteOrderDevolutionsUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
