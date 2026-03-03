package com.essenza.draco.modules.inventory.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryMovementDto {
    @NotNull
    private Long productId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    @NotNull
    private String type; // ENTRY, EXIT, TRANSFER
    @NotNull
    @Min(1)
    private Integer quantity;
    private String reason;
}
