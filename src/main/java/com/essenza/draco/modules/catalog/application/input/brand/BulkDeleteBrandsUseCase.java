package com.essenza.draco.modules.catalog.application.input.brand;

import java.util.List;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Eliminación (lógica) de varios registros; cada id se procesa por separado. */
public interface BulkDeleteBrandsUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
