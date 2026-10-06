package com.essenza.draco.modules.catalog.application.input.product_child;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import java.util.List;

public interface BulkDeleteProductChildrenUseCase {
    BulkOperationResult deleteAll(List<Long> ids);
}
