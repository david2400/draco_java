package com.essenza.draco.modules.devolution.domain.dto.motive_devolution;

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
public class UpdateMotiveDevolutionDto extends CreateMotiveDevolutionDto {
    @NotNull
    @Positive
    private Long id;
}
