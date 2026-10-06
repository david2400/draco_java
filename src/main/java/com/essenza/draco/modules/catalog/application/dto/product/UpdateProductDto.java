package com.essenza.draco.modules.catalog.application.dto.product;

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
public class UpdateProductDto extends CreateProductDto {
    /** Ignorado: el id lo fija la URL ({@code PUT /products/{id}}). */
    @Positive
    private Long id;
}
