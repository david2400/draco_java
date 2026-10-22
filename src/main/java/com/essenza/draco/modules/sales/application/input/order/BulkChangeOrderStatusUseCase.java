package com.essenza.draco.modules.sales.application.input.order;

import com.essenza.draco.modules.sales.application.dto.order.BulkOrderStatusDto;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

/** Cambia el estado de varias órdenes; los fallos se informan por orden. */
public interface BulkChangeOrderStatusUseCase {
    BulkOperationResult changeAll(BulkOrderStatusDto input);
}
