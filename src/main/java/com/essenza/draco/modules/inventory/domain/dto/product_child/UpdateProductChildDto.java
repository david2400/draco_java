package com.essenza.draco.modules.inventory.domain.dto.product_child;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductChildDto extends CreateProductChildDto {
    @NotNull
    @Positive
    private Long id;
}
