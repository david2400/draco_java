package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;

import java.util.List;

public interface DeleteWarehouseUseCase {
    /** 409 si es la principal, tiene stock o la usan tiendas o cajas. */
    void delete(Long id);

    BulkOperationResult deleteAll(List<Long> ids);
}
