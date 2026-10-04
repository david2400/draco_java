package com.essenza.draco.modules.catalog.application.input.subcategory;

import java.util.List;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Eliminación (lógica) de varios registros; cada id se procesa por separado. */
public interface BulkDeleteSubcategoriesUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
