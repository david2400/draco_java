package com.essenza.draco.modules.catalog.domain.dto.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBrandDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String slug;
}
