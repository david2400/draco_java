package com.essenza.draco.modules.inventory.domain.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseDto extends CreateWarehouseDto {
    @NotNull
    @Positive
    private Long id;
}
