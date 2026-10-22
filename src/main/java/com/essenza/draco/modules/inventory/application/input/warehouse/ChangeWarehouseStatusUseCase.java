package com.essenza.draco.modules.inventory.application.input.warehouse;

import com.essenza.draco.modules.inventory.application.dto.warehouse.WarehouseDto;

public interface ChangeWarehouseStatusUseCase {
    /** Activar o desactivar; 409 si es la principal, tiene reservas o la usa una tienda activa. */
    WarehouseDto setActive(Long id, boolean active);
}
