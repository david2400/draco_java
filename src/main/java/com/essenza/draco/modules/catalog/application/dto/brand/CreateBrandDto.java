package com.essenza.draco.modules.catalog.application.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 150)
    private String name;

    @Size(max = 1000)
    private String description;

    /** Opcional: si llega vacío se genera a partir del nombre. */
    @Size(max = 180)
    @Pattern(regexp = "^$|^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "solo minúsculas, números y guiones")
    private String slug;
}
