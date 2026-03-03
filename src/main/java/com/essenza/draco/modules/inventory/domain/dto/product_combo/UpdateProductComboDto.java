package com.essenza.draco.modules.inventory.domain.dto.product_combo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductComboDto extends CreateProductComboDto {
    @NotNull
    private Long id;
}
