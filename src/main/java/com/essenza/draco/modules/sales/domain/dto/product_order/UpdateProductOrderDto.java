package com.essenza.draco.modules.sales.domain.dto.product_order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductOrderDto extends CreateProductOrderDto {
    @NotNull
    private Long id;
}
