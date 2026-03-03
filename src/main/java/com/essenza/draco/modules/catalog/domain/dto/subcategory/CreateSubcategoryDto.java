package com.essenza.draco.modules.catalog.domain.dto.subcategory;

import jakarta.validation.constraints.NotBlank;
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
public class CreateSubcategoryDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String slug;
    @NotNull
    @Positive
    private Long categoryId;
}
