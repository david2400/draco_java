package com.essenza.draco.modules.inventory.domain.dto.supplier;

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
public class UpdateSupplierDto extends CreateSupplierDto {
    @NotNull
    @Positive
    private Long id;
}
