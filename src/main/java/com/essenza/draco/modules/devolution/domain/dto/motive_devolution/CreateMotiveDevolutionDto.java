package com.essenza.draco.modules.devolution.domain.dto.motive_devolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMotiveDevolutionDto {
    @NotBlank
    private String name;
    private String description;
}
