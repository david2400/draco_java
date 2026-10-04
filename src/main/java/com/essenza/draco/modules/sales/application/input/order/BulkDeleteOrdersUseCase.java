package com.essenza.draco.modules.sales.application.input.order;

import java.util.List;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Eliminación (lógica) en lote; cada id se valida por separado. */
public interface BulkDeleteOrdersUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
