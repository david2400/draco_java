package com.essenza.draco.modules.devolution.application.dto.evidence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEvidenceDto extends CreateEvidenceDto {
    @NotNull
    @Positive
    private Long id;
}
