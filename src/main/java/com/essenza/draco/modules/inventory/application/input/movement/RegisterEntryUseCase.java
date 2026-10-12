package com.essenza.draco.modules.inventory.application.input.movement;

import com.essenza.draco.modules.inventory.domain.dto.InventoryMovementDto;

public interface RegisterEntryUseCase {
    InventoryMovementDto registerEntry(InventoryMovementDto dto);
}
