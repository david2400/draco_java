package com.essenza.draco.modules.inventory.application.input.supplier;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

import java.util.List;

public interface BulkDeleteSuppliersUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
