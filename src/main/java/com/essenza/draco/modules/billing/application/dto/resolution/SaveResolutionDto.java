package com.essenza.draco.modules.billing.application.dto.resolution;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveResolutionDto {
    @NotBlank
    private String kind;
    private String prefix;
    private String resolutionNumber;
    private LocalDate resolutionDate;
    private String technicalKey;
    @NotNull
    private Long fromNumber;
    @NotNull
    private Long toNumber;
    /** Vacío = el número inicial (alta) o el actual (edición). */
    private Long nextNumber;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String environment;
    private Boolean active;
}
