package com.essenza.draco.modules.devolution.domain.dto.return_method;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReturnMethodDto {
    @NotBlank
    private String name;

    private String description;

    private String instructions;

    private Boolean active;
}
