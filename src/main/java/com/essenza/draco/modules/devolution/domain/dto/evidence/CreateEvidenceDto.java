package com.essenza.draco.modules.devolution.domain.dto.evidence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvidenceDto {
    @NotNull
    @Positive
    private Long orderDevolutionId;

    @NotBlank
    private String evidenceType;

    @NotBlank
    private String resourceUrl;

    private String description;

    private String recordedBy;

    private LocalDateTime recordedAt;
}
