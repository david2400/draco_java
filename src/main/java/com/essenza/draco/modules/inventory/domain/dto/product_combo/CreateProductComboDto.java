package com.essenza.draco.modules.inventory.domain.dto.product_combo;

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
public class CreateProductComboDto {
    @NotNull
    @Positive
    private Integer quantity;
    @NotNull
    @Positive
    private Long productId;
    @NotNull
    @Positive
    private Long comboId;
}
