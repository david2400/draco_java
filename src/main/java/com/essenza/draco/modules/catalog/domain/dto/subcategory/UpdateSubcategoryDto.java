package com.essenza.draco.modules.catalog.domain.dto.subcategory;

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
public class UpdateSubcategoryDto extends CreateSubcategoryDto {
    @NotNull
    @Positive
    private Long id;
}
